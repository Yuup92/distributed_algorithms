package tudelft.da;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * This is the Interface for the first assignment of Distributed Algorithms
 * All remotely extend accessible methods are declared here
 */
public interface DA_Schiper_Eggli_Sandoz_RMI extends Remote {

    public void sendMessage(String url, String message, int r_procID) throws RemoteException;

    public void receive(Message message) throws RemoteException;

    public String getUrl() throws RemoteException;

    public void setDelay(boolean t) throws RemoteException;

}
