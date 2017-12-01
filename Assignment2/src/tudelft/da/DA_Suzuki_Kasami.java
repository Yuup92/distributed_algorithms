package tudelft.da;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadFactory;

/**
 * Created by yuupv on 29-Nov-17.
 */
public class DA_Suzuki_Kasami extends UnicastRemoteObject
    implements DA_Suzuki_Kasami_RMI, Runnable{

    private boolean inCriticalSection;

    private int ID;
    private int numOfProcInNetwork;
    private String url;
    private ArrayList<String> urls;

    private ArrayList<Integer> N;

    private Token token;

    public DA_Suzuki_Kasami(int processNumber, String url, ArrayList<String> urls, int numOfProcInNetwork) throws RemoteException {

        this.ID = processNumber;
        this.numOfProcInNetwork = numOfProcInNetwork;
        this.url = url;
        this.urls = urls;

        this.N = initializeN();

        this.inCriticalSection = false;
        this.token = null;
    }

    public void run() {
        System.out.println("Process " + ID + " has started");
    }

    public void broadcastRequest() {
        this.N.set(this.ID, this.N.get(this.ID) + 1);

        try {
            for(String url : urls) {
                DA_Suzuki_Kasami_RMI receiver = (DA_Suzuki_Kasami_RMI) Naming.lookup(url);
                receiver.receiveRequest(this.url, this.ID, this.N);
            }
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            System.out.println("During broadcast of process: " + ID + " an error ocurred, err: " + e);
            e.printStackTrace();
        }
    }

    public void receiveToken(Token token) {
        this.token = token;

        if( token != null &&
                !inCriticalSection &&
                (this.token.getTN().get(this.ID) < this.N.get(this.ID))) {
            enterCS();
        }
    }

    private void releaseToken() {
        this.token.getTN().set(this.ID, this.N.get(this.ID));
        if(this.token != null && !inCriticalSection) {
            System.out.println("Should pass this point N: " + N.toString() + "  TN: " +  token.getTN().toString());
            for (int i = 0; i < this.numOfProcInNetwork; i++) {
                if(this.ID == i){
                    continue;
                } else if(this.N.get(i) > this.token.getTN().get(i)) {

                    sendToken(this.urls.get(i), i);
                    break;
                }
            }
        }
    }

    public void sendToken(String url, int src_ID) {
        System.out.println("Token is being released from process " + ID + " being sent to process " + src_ID + " current state of token is: " + token.getTN().toString() + " current state of proc " + N.toString());
        try {
            DA_Suzuki_Kasami_RMI receiver = (DA_Suzuki_Kasami_RMI) Naming.lookup(url);
            receiver.receiveToken(this.token);
            this.token = null;
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            System.out.println("Something went wrong when sending the token, err: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void receiveRequest(String s_url, int srcID, ArrayList<Integer> s_N)  {
        //Accepts the request and adds it to N
        this.N.set(srcID, s_N.get(srcID));
        System.out.println("Process " + ID + " has N " + N.toString());
        //Checks a couple of conditions to make sure it can send the token
        if(this.token != null &&
                !this.inCriticalSection &&
                ID != srcID &&
                s_N.get(srcID) > this.token.getTN().get(srcID) ) {
            sendToken(s_url, srcID);
        }
    };

    public void enterCS()  {
        this.inCriticalSection = true;
        System.out.println("Process" + ID + " is in the critical section");

        try {
            Thread.sleep(new Random().nextInt(2000));
        } catch (InterruptedException e) {
            System.out.println("Something went wrong in critical section trying to sleep the thread, err: " + e);
        }

        this.inCriticalSection = false;
        releaseToken();



    };

    public ArrayList<Integer> initializeN() {

        ArrayList<Integer> N = new ArrayList<>();

        for (int i = 0; i < this.numOfProcInNetwork; i++) {
            N.add(0);
        }

        return N;
    }

    public void printN() {
        System.out.println("Process " + ID + " " + this.N.toString());
    }


}
