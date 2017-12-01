package tudelft.da;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by yuupv on 23-Nov-17.
 */
public class DA_Schiper_Eggli_Sandoz extends UnicastRemoteObject
        implements DA_Schiper_Eggli_Sandoz_RMI, Runnable {

    /**
     * Initialization of empty variables, lists and maps
     */
    private int processNumber;
    private int numOfProcInNetwork;
    private String url;
    private boolean delay = false;

    private int currentClock;
    private ArrayList<Integer> timeStamp;
    private ArrayList<Buffer_Element> S;

    private ArrayList<Message> mBuffer;


    public DA_Schiper_Eggli_Sandoz(int processNumber, String url, int numOfProcInNetwork) throws RemoteException{
        this.processNumber = processNumber;
        this.url = url;
        this.numOfProcInNetwork = numOfProcInNetwork;

        this.currentClock = 0;
        this.timeStamp = DA_Schiper_Eggli_Sandoz.initializeVC(numOfProcInNetwork);

        this.S = initializeBuffer();
        this.mBuffer = new ArrayList<>();
    }

    public void run() {
        System.out.println("Process " + processNumber + " is running ");
    }



    public void sendMessage(String url, String message, int r_procID) {
        incrementVectorClock();

        ArrayList<Buffer_Element> t = new ArrayList<>();
        for (int i = 0; i < numOfProcInNetwork; i++) {
            t.add(this.S.get(i));
        }
        Message m = new Message(this.processNumber, r_procID, message, t, this.timeStamp, this.delay);
        DA_Schiper_Eggli_Sandoz_RMI reciever = null;

        try{
            reciever = (DA_Schiper_Eggli_Sandoz_RMI) Naming.lookup(url);

            reciever.receive(m);

            updateBuffer(r_procID);
            System.out.println("Process " + processNumber + " has sent a message to process " + r_procID);

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            System.out.println("Something went wrong in the sendMessage function for process " + this.processNumber + ", err: " + e);
            e.printStackTrace();
        }

    }

    public synchronized void receive(Message message) throws RemoteException {

        if (message.getDelay()) {
            new Threzad(new Delay(this, message)).start();
            return;
        }

        if(deliveryAllowed(message, false)) {
            System.out.println("Process " + processNumber + " has recieved a message from process: " + message.getSentProcessID());
            incrementVectorClock();
            mergeTimeStamps(message.getTimestamp());
            mergeBuffers(message.getBuffer(), message.getSentProcessID());
            ArrayList<Message> mRemoved = new ArrayList<>();


            for(Message m : this.mBuffer) {
                if(deliveryAllowed(m, true)){
                    System.out.println("Checking");
                    incrementVectorClock();
                    mergeTimeStamps(m.getTimestamp());
                    mRemoved.add(m);
                    System.out.println("Process " + processNumber + " has recieved a message from process: " + m.getSentProcessID() + " from buffer");
                }
            }
            this.mBuffer.removeAll(mRemoved);

        } else {
            this.mBuffer.add(message);
            System.out.println("Message has been added to the buffer in process " + processNumber);
        }



    }

    private boolean deliveryAllowed(Message m, boolean b) {
        /**
         * This area is where the bug is happening!!! Fix this.
         */


        Buffer_Element r_V = m.getBufferElement(this.processNumber);

        if(m.getReceiveProcessID() == this.processNumber) {
            if(!r_V.compareTimeStamps(this.timeStamp)){
                return false;
            }
        }

        return true;
    }

    private void mergeTimeStamps(ArrayList<Integer> timeStamp) {
        for (int i = 0; i <timeStamp.size() ; i++) {
            if(timeStamp.get(i) > this.timeStamp.get(i)){
                this.timeStamp.set(i,timeStamp.get(i));
            }
        }
    }

    private void mergeBuffers(ArrayList<Buffer_Element> r_S, int f) {

        for (int i = 0; i < r_S.size(); i++) {
            if(this.S.get(i).compareVectorClock(r_S.get(i).getVectorClock())) {
                //System.out.println("P" + f + "  " + r_S.get(i).getVectorClock().toString());
                this.S.get(i).updateVC(r_S.get(i).getVectorClock());
            }
        }
    }

    public void setDelay(boolean t) {
        this.delay = t;
    }

    private void incrementVectorClock() {
        this.currentClock++;
        this.timeStamp.set(this.processNumber, this.currentClock);
    }

    private void updateBuffer(int r_procID) {
        ArrayList<Integer> f = new ArrayList<>();
        for (int i = 0; i < numOfProcInNetwork; i++) {
            f.add(this.timeStamp.get(i));
        }
        if(this.S.get(r_procID).getProcessNumber() == r_procID){
            this.S.get(r_procID).updateVC(f);
        } else {
            System.out.println("update buffer seems to be in the wrong order");
        }

    }

    public String getUrl() {
        return this.url;
    }

    private ArrayList<Buffer_Element> initializeBuffer() {

        ArrayList<Buffer_Element> S = new ArrayList<>();

        for (int i = 0; i < this.numOfProcInNetwork; i++) {
            S.add(new Buffer_Element(i, this.numOfProcInNetwork));
        }

        return S;
    }

    /************************************************
     ** Static functions
     ***********************************************/

    /**
     * This function initializes a vector clock with all zero's
     */
    private static ArrayList<Integer> initializeVC(int numOfProcInNetwork) {
        ArrayList<Integer> vectorClock = new ArrayList<>();

        for (int i = 0; i < numOfProcInNetwork ; i++) {
            vectorClock.add(0);
        }

        return vectorClock;
    }
}
