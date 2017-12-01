package tudelft.da;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuupv on 28-Nov-17.
 */

/**
 * Message has to implment Serializable otherwise MarshalException occurs
 */
public class Message implements Serializable {


    private int s_Proc;
    private int r_Proc;
    private boolean delay;

    private String message;
    private ArrayList<Buffer_Element> S;
    private ArrayList<Integer> timestamp;

    public Message(int sender, int receiver, String m, ArrayList<Buffer_Element> buffer, ArrayList<Integer> ts, boolean delay) {

        this.s_Proc = sender;
        this.r_Proc = receiver;

        this.message = m;
        this.S = buffer;
        this.timestamp = ts;
        this.delay = delay;

    }

    public Buffer_Element getBufferElement(int procID) {
        return S.get(procID);
    }

    public ArrayList<Buffer_Element> getBuffer() {
        return this.S;
    }

    public int getReceiveProcessID() {
        return r_Proc;
    }

    public int getSentProcessID() {
        return s_Proc;
    }

    public ArrayList<Integer> getTimestamp() {
        return this.timestamp;
    }

    public void setDelay(boolean d){
        this.delay = d;
    }

    public boolean getDelay(){
        return this.delay;
    }


}
