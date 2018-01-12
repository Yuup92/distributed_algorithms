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
import java.rmi.NotBoundException;
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
    private static final String IP2 = "172.25.90.17";
    private static final int SERVERPORT = 1099;

    private static final String RMI_REMOTE_IP = "";


    public ProcessManager() {
        this.procList = new ArrayList<>();
    }

    public void startNetwork() {

        String[] urls = new String[NUMBEROFPROCESSES];

        if(true) {
            if(false){
                // Starts Server
                urls = startRemoteServer();
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

        //procList.get(3).wakeUp();

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

    public String[] startRemoteServer() {

        //VM: -Djava.security.policy=file:./java.policy -Djava.rmi.server.hostname="172.25.90.17" -Djava.security.debug=access,failure

        //System.setProperty("java.security.policy", "D:/Trial/distributed_algorithms/Assignment3/java.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            String name = "Server";
            DA_Gallager_Humblet_Spira engine = new DA_Gallager_Humblet_Spira();
//            DA_Gallager_Humblet_Spira stub =
//                    (DA_Gallager_Humblet_Spira) UnicastRemoteObject.exportObject(engine, 0);
            registry = LocateRegistry.getRegistry(1099);
            registry.rebind(name, engine);
            System.out.println("ComputeEngine bound");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }


        try {
            inetAddress = InetAddress.getLocalHost();
            System.out.println(inetAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String[] urls = new String[NUMBEROFPROCESSES];

        for (int i = 0; i < NUMBEROFPROCESSES; i++) {
            urls[i] = RMI_PREFIX + RMI_LOCALHOST + RMI_PROCESS + i;
        }

        return urls;

    }

    public String[] connectToRemoteServer(){

        //VM parameters: -Djava.security.policy=file:./java.policy

        String[] urls = new String[NUMBEROFPROCESSES];

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        System.setProperty("java.rmi.server.hostname","192.168.1.42");

        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry = LocateRegistry.getRegistry(IP, SERVERPORT);
            DA_Gallager_Humblet_Spira process = (DA_Gallager_Humblet_Spira) registry.lookup("Server");
            process.sayHello();
            System.out.println("Hey man");
            //urls = registry.list();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        return urls;

    }

}

/**
 DA_Gallager_Humblet_Spira process;


 Registry registry;

 try {
 //Runtime.getRuntime().exec("rmiregistry 1099");
 registry =  LocateRegistry.createRegistry(1099);
 } catch (RemoteException e) {
 System.out.println("Could not create a registry on port 1099, error "  + e);
 e.printStackTrace();
 }

 //System.setProperty("java.security.policy", "D:/Trial/distributed_algorithms/Assignment3/java.policy");


 String[] urls = new String[NUMBEROFPROCESSES];

 for (int i = 0; i < NUMBEROFPROCESSES; i++) {
 urls[i] = RMI_PREFIX + RMI_LOCALHOST + RMI_PROCESS + i;
 }


 System.out.println(inetAddress.getHostAddress());

 //        String[] urls = null;
 //
 //        if(config != null){
 //            urls = config.getStringArray("node.url");
 //        }
 try {
 //Registry reg = LocateRegistry.createRegistry(1099);
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
 */