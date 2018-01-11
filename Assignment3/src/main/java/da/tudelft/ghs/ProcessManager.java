package da.tudelft.ghs;

import da.tudelft.Network.CircleNetwork;
import da.tudelft.Network.FullNetwork;
import da.tudelft.datastructures.Node;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ProcessManager  {


    private ArrayList<DA_Gallager_Humblet_Spira> procList;
    private ArrayList<Node> nodeList;


    /**
     * Configurations Initial Variables
     */
    private InetAddress inetAddress;

    /**
     * Static and constant variables defined below,
     * these values should not need to change.
     */
    private static final int NUMBEROFPROCESSES = 10;
    private static final String RMI_PREFIX = "rmi://";
    private static final String RMI_LOCALHOST = "localhost";
    private static final String RMI_PROCESS = "/process_";
    private static final String IP = "192.168.1.42";
    private static final int SERVERPORT = 1099;

    private static final String RMI_REMOTE_IP = "";


    public ProcessManager() {
        this.procList = new ArrayList<>();
    }

    public void startNetwork() {

        String[] urls = new String[NUMBEROFPROCESSES];

        if(false) {
            if(true){
                // Starts Server
                urls = useConfigurationFile();
            } else {
                urls = connectToRemoteServer();
            }

        } else {
           urls = useLocalDistributedSystem(urls);

        }

        //this.nodeList = new FullNetwork().createFullNetwork(NUMBEROFPROCESSES, urls);
        this.nodeList = new CircleNetwork().createCircularNetwork(NUMBEROFPROCESSES, urls);

        for (int i = 0; i < NUMBEROFPROCESSES; i++) {
            procList.get(i).addNode(nodeList.get(i));
        }

        procList.get(3).wakeUp();

        //procList.get(3).sendMessage(urls[2], " This is " + urls[3] + " checking in!");


    }

    public String[] useLocalDistributedSystem(String[] url){

        DA_Gallager_Humblet_Spira process;

        //TODO Remember to remove this
       // if(true){
            String[] urls = new String[NUMBEROFPROCESSES];

            for (int i = 0; i < NUMBEROFPROCESSES; i++) {
                urls[i] = RMI_PREFIX + RMI_LOCALHOST + RMI_PROCESS + i;
            }
        //}

        try {
            LocateRegistry.createRegistry(1099);
            for (int i = 0; i < NUMBEROFPROCESSES; i++) {
                process = new DA_Gallager_Humblet_Spira(i, urls[i]);
                new Thread((DA_Gallager_Humblet_Spira) process).start();
                Naming.bind(urls[i], process);
                procList.add(process);
            }
        } catch (RemoteException | AlreadyBoundException | MalformedURLException e) {
            System.out.println("Processes did not want to start, error: " + e);
            e.printStackTrace();
        }

        return urls;

    }

    public String[] useConfigurationFile() {

        DA_Gallager_Humblet_Spira process;

        /**
         * Starts a registry on port 1099
         */
//        try {
//            LocateRegistry.createRegistry(1099);
//        } catch (RemoteException e) {
//            System.out.println("Could not create a registry on port 1099, error "  + e);
//            e.printStackTrace();
//        }

        /**
         * System.setProperty() needs to be run before System.getSecurityManager()
         *   Otherwise an exception will be thrown for not having access to the java.policy file
         *
         */

        //System.setProperty("java.security.policy", "D:/Trial/distributed_algorithms/Assignment3/java.policy");
        //        Configuration config = null;
//        try{
//            config = new PropertiesConfiguration("network.cfg");
//        } catch (ConfigurationException e) {
//            e.printStackTrace();
//        }

        String[] urls = new String[NUMBEROFPROCESSES];

        for (int i = 0; i < NUMBEROFPROCESSES; i++) {
            urls[i] = RMI_PREFIX + RMI_LOCALHOST + RMI_PROCESS + i;
        }

        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println(inetAddress.getHostAddress());

//        String[] urls = null;
//
//        if(config != null){
//            urls = config.getStringArray("node.url");
//        }
        try {
            Registry reg = LocateRegistry.createRegistry(1099);
            for (int i = 0; i < NUMBEROFPROCESSES; i++) {
                process = new DA_Gallager_Humblet_Spira(i, urls[i]);
                //DA_Gallager_Humblet_Spira stub = (DA_Gallager_Humblet_Spira) UnicastRemoteObject.exportObject(process, i);
                new Thread((DA_Gallager_Humblet_Spira) process).start();
                String p = "process" + i;
                reg.rebind(p, process);
                //Naming.bind(urls[i], process);
                procList.add(process);
            }
        } catch (RemoteException e) {
            System.out.println("Processes did not want to start, error: " + e);
            e.printStackTrace();
        }

        int i = 1;
        for (String url : urls) {
            System.out.println(i + " url " + url);
            i++;
        }

        return urls;

    }

    public String[] connectToRemoteServer(){

        //VM parameters: -Djava.security.policy=file:./java.policy

        String[] urls = new String[NUMBEROFPROCESSES];

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {

            Registry registry = LocateRegistry.getRegistry(IP, SERVERPORT);
            urls = registry.list();
            int i = 1;
            for (String url : urls) {
                System.out.println(i + " url " + url);
                i++;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }



//        String[] urls = new String[NUMBEROFPROCESSES];
//
//        for (int i = 0; i < NUMBEROFPROCESSES; i++) {
//            urls[i] = RMI_PREFIX + IP + RMI_PROCESS + i;
//        }
//
//        useLocalDistributedSystem(urls);



        return urls;

    }

}
