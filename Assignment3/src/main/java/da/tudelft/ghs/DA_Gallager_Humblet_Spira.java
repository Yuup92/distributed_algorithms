package da.tudelft.ghs;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DA_Gallager_Humblet_Spira extends UnicastRemoteObject
        implements DA_Gallager_Humblet_Spira_RMI, Runnable {

    private int processID;

    private String url;

    public DA_Gallager_Humblet_Spira(int ID, String url) throws RemoteException{
        this.processID = ID;
        this.url = url;
    }

    public void sendMessage(String url, String message) {

    }

    public synchronized void receive(String message) {
        System.out.println();
    }


    public void run() {

    }
}
