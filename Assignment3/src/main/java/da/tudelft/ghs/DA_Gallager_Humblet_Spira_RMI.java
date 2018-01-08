package da.tudelft.ghs;

import da.tudelft.bufferMessage.ConnectMessage;
import da.tudelft.bufferMessage.ReportMessage;
import da.tudelft.bufferMessage.TestMessage;
import da.tudelft.datastructures.Node;
import org.junit.Test;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DA_Gallager_Humblet_Spira_RMI extends Remote {

    public void sendConnect(String url, int levelFragment) throws RemoteException;

    public void receiveConnect(ConnectMessage cM) throws RemoteException;

    public void sendInitiate(String url, int nodeLevel, int fragmentName, int nodeState, int nodeNumber) throws RemoteException;

    public void receiveInitiate(int s_nodeLevel, int s_fragmentName, int s_nodeState, int s_nodeNumber) throws RemoteException;

    public void sendTest() throws RemoteException;

    public void receiveTest(TestMessage testMessage) throws RemoteException;

    public void receiveReject(int s_NN) throws RemoteException;

    public void receiveAccept(int s_NN) throws RemoteException;

    public void sendReport() throws RemoteException;

    public void receiveReport(ReportMessage reportMessage) throws RemoteException;

    public void sendChangeRoot() throws RemoteException;

    public void receiveChangeRoot() throws RemoteException;

    public void sendMessage(String url, String message) throws RemoteException;

    public void receive(String message) throws RemoteException;

}
