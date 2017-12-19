package da.tudelft.ghs;

import da.tudelft.datastructures.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DA_Gallager_Humblet_Spira_RMI extends Remote {

    public void wakeup(int nodeNumber) throws RemoteException;

    public void sendMessage(int receiverNodeNumber, int senderNodeNumber, String messageType, int Level, int Fragment, String nodeStatus) throws RemoteException;

    public void receiveMessage(Message message) throws RemoteException;

    public String getUrl() throws RemoteException;

    public void initiate(int Level, int Fragment, int nodeState) throws RemoteException;

}
