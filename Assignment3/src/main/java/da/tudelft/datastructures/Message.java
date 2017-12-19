package da.tudelft.datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import da.tudelft.datastructures.Node;

public class Message implements Serializable {

    private String messageType;
    private int s_Node;
    private int r_Node;
    private int Level;
    private int Fragment;
    private int s_Status;

    public Message(int senderNodeNumber, int receiverNodeNumber, String mType, int L, int F, int S) {
        this.messageType = mType;
        this.s_Node = senderNodeNumber;
        this.r_Node = receiverNodeNumber;
        this.Level = L;
        this.Fragment = F;
        this.s_Status = S;

    }

    public int getSenderNodeNumber(){
        return s_Node;
    }

    public int getReceiverNodeNumber(){
        return r_Node;
    }

    public String getMessageType() {
        return messageType;
    }

    public int getFragment() {
        return Fragment;
    }

    public int getLevel(){
        return Level;
    }
}
