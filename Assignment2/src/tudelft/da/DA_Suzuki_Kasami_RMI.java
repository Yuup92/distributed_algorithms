package tudelft.da;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by yuupv on 29-Nov-17.
 */
public interface DA_Suzuki_Kasami_RMI extends Remote {


    public void broadcastRequest() throws RemoteException;

    public void receiveRequest(String s_url, int srcID, ArrayList<Integer> s_N) throws RemoteException;

    public void receiveToken(Token token) throws RemoteException;

    public void sendToken(String url, int i) throws RemoteException;

    public void enterCS() throws RemoteException;




}
