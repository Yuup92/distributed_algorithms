package da.tudelft.bufferMessage;

import java.io.Serializable;

/**
 * Created by yuupv on 08-Jan-18.
 */
public class BufferMessage implements Serializable {

    protected int s_NN;
    protected int messageType;

    protected final static int CONNECT = 0;
    protected final static int REPORT = 1;
    protected final static int TEST = 2;

    public BufferMessage(int s_NN) {
        this.s_NN = s_NN;

    }

    public int getNodeNumber() {
        return s_NN;
    }

}
