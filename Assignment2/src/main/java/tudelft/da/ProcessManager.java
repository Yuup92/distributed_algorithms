package tudelft.da;

import javax.naming.ConfigurationException;
import javax.security.auth.login.Configuration;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by yuupv on 29-Nov-17.
 */
public class ProcessManager {


    private static final int numberOfProcesses = 5;

    private ArrayList<DA_Suzuki_Kasami> procList;

    /**
     * Static and constant variables defined below,
     * these values should not need to change.
     */
    private static final String RMI_PREFIX = "rmi://";
    private static final String RMI_LOCALHOST = "localhost/";
    private static final String RMI_PROCESS = "process_";

    public ProcessManager(){

    }

    public void startNetwork() {


        tudelft.da.DA_Suzuki_Kasami process;
        procList = new ArrayList<>();

        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < numberOfProcesses; i++) {
            String url = RMI_PREFIX + RMI_LOCALHOST + RMI_PROCESS + i;
            urls.add(url);
        }

        try {
            for (int i = 0; i < this.numberOfProcesses ; i++) {
                process = new DA_Suzuki_Kasami(i, urls.get(i), urls, numberOfProcesses);
                new Thread( (DA_Suzuki_Kasami)process).start();
                Naming.bind(urls.get(i), process);
                procList.add(process);

            }
        } catch (RemoteException | AlreadyBoundException | MalformedURLException e) {
            System.out.println("Something went wrong trying to bind one of the processes, error: " + e);
            e.printStackTrace();
        }
        testNetwork();
    }

    public void testNetwork() {
        Token t = Token.getToken(numberOfProcesses);


        for (int i = 0; i < numberOfProcesses; i++) {
            procList.get(i).broadcastRequest();
        }

        procList.get(3).receiveToken(t);

        procList.get(3).broadcastRequest();
        procList.get(2).broadcastRequest();


    }

}
