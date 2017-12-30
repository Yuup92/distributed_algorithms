package da.tudelft.ghs;

import da.tudelft.datastructures.Node;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DA_Gallager_Humblet_Spira_RMI extends Remote {

    public void sendConnect(String url, int levelFragment) throws RemoteException;

    public void receiveConnect(int levelFragmentSender, Node senderNodex) throws RemoteException;

    public void sendInitiate(String url, int nodeLevel, int fragmentName, int nodeState, int nodeNumber) throws RemoteException;

    public void receiveInitiate(int s_nodeLevel, int s_fragmentName, int s_nodeState, int s_nodeNumber) throws RemoteException;

    public void sendMessage(String url, String message) throws RemoteException;

    public void receive(String message) throws RemoteException;

}
