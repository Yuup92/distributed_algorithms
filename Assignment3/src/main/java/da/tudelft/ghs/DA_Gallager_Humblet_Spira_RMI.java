package da.tudelft.ghs;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DA_Gallager_Humblet_Spira_RMI extends Remote {

    public void sendMessage(String url, String message) throws RemoteException;

    public void receive(String message) throws RemoteException;

}
