package da.tudelft.bufferMessage;

import java.io.Serializable;

/**
 * Created by yuupv on 08-Jan-18.
 */
public class TestMessage extends  BufferMessage implements Serializable {

    private int s_NN;
    private int s_LN;
    private int s_FN;

    public TestMessage(int s_NodeNumber, int s_NodeLevel, int s_FragementName){

        super(s_NodeNumber);

        this.messageType = TEST;

        this.s_NN = s_NodeNumber;
        this.s_LN = s_NodeLevel;
        this.s_FN = s_FragementName;

    }

    public int getNodeLevel() {
        return s_LN;
    }

    public int getFragementName() {
        return s_FN;
    }
}
