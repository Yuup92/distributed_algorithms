package tudelft.da;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by yuupv on 23-Nov-17.
 */
public class ProcessManager {

    /**
     * Initial variables for the Process manager
     */
    private static final int numberOfProcesses = 3;

    /**
     * Initialization of empty variables, lists and maps
     */
    private ArrayList<DA_Schiper_Eggli_Sandoz_RMI> procList;

    /**
     * Static and constant variables defined below,
     * these values should not need to change.
     */
    private static final String RMI_PREFIX = "rmi://";
    private static final String RMI_LOCALHOST = "localhost/";
    private static final String RMI_PROCESS = "process_";

    public ProcessManager() {

    }

    /**
     * In this function the server and all processes with be started and added to the
     * RMI registry. Each process is started in its own thread.
     */
    public void startNetwork() {
        DA_Schiper_Eggli_Sandoz_RMI process;
        procList = new ArrayList<DA_Schiper_Eggli_Sandoz_RMI>();

        try{
            for (int i = 0; i < numberOfProcesses; i++) {
                String url = RMI_PREFIX + RMI_LOCALHOST + RMI_PROCESS + i;
                process = new DA_Schiper_Eggli_Sandoz(i, url, numberOfProcesses);

                /**
                 * Thread casts DA_Schiper_Eggli_Sandoz because it implements Runnable
                 * Runnable allows classes to run as a thread.
                 * DA_Schiper_Eggli_Sandoz_RMI can not implement a runnable seeing as it
                 *  is an interface.
                 */
                new Thread((DA_Schiper_Eggli_Sandoz) process).start();
                Naming.bind(url, process);
                procList.add(process);
            }

        } catch (RemoteException | AlreadyBoundException | MalformedURLException e ) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        testSendMessage();
    }

    public void testSendMessage() {
        try{
            procList.get(0).setDelay(true);
            procList.get(0).sendMessage(procList.get(1).getUrl(), "Yo soy", 1);
            procList.get(0).setDelay(false);
            procList.get(0).sendMessage(procList.get(2).getUrl(), "Hi", 2);
            procList.get(2).sendMessage(procList.get(1).getUrl(), "Hello", 1);
        } catch (RemoteException e) {
            System.out.println("Test failed due to remoteexception....");
            e.printStackTrace();
        }
    }

}
