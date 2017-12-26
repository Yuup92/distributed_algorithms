package da.tudelft.ghs;

import da.tudelft.Network.CircleNetwork;
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
    private static final String RMI_LOCALHOST = "localhost/";
    private static final String RMI_PROCESS = "process_";

    private static final String RMI_REMOTE_IP = "";


    public ProcessManager() {

    }

    public void startNetwork() {

        String[] urls = new String[NUMBEROFPROCESSES];

        if(false) {
            if(false){
                urls = useConfigurationFile();
            } else {
                urls = connectToRemoteServer();
            }

        } else {
           urls = useLocalDistributedSystem();
        }

        this.nodeList = new CircleNetwork().createCircularNetwork(NUMBEROFPROCESSES, urls);

    }

    public String[] useLocalDistributedSystem(){

        DA_Gallager_Humblet_Spira process;
        String[] urls = new String[NUMBEROFPROCESSES];

        for (int i = 0; i < NUMBEROFPROCESSES; i++) {
            urls[i] = RMI_PREFIX + RMI_LOCALHOST + RMI_PROCESS + i;
        }

        try {
            for (int i = 0; i < NUMBEROFPROCESSES; i++) {
                process = new DA_Gallager_Humblet_Spira(i, urls[i]);
                new Thread((DA_Gallager_Humblet_Spira) process).start();
                Naming.bind(urls[i], process);
            }
        } catch (RemoteException | AlreadyBoundException | MalformedURLException e) {
            System.out.println("Processes did not want to start, error: " + e);
            e.printStackTrace();
        }

        return urls;

    }

    public String[] useConfigurationFile() {

        Configuration config = null;
        try{
            config = new PropertiesConfiguration("network.cfg");
        } catch (ConfigurationException e) {

        }

        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println(inetAddress.getHostAddress());

        String[] urls = null;

        if(config != null){
            urls = config.getStringArray("node.url");
        }


        int i = 1;
        for (String url : urls) {
            System.out.println(i + " url " + url);
            i++;
        }

        return urls;

    }

    public String[] connectToRemoteServer(){

        String[] urls = null;

        return urls;

    }




}
