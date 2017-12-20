package da.tudelft.ghs;

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
import java.util.HashMap;
import java.util.TreeSet;

public class ProcessManager  {


    private ArrayList<DA_Gallager_Humblet_Spira> procList;


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


    public ProcessManager() {

    }

    public void startNetwork() {

        if(false) {
            useConfigurationFile();
        } else {
            useLocalDistributedSystem();
        }




    }

    public void useLocalDistributedSystem(){

        DA_Gallager_Humblet_Spira process;
        String[] urls = new String[NUMBEROFPROCESSES];

        for (int i = 0; i < NUMBEROFPROCESSES; i++) {
            urls[i] = RMI_PREFIX + RMI_LOCALHOST + RMI_PROCESS + i;
        }




        try {
            for (int i = 0; i < NUMBEROFPROCESSES; i++) {
                process = new DA_Gallager_Humblet_Spira();
                new Thread((DA_Gallager_Humblet_Spira) process).start();
                Naming.bind(urls[i], process);
            }
        } catch (RemoteException | AlreadyBoundException | MalformedURLException e) {

        }



    }

    public void useConfigurationFile() {

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


    }

    public void circleNetwork() {

    }


}