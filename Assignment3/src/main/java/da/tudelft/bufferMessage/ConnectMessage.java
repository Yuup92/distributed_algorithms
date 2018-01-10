package da.tudelft.bufferMessage;

import da.tudelft.datastructures.Node;

import java.io.Serializable;

/**
 * Created by yuupv on 08-Jan-18.
 */
public class ConnectMessage extends BufferMessage implements Serializable {

    private int s_LN;
    private Node s_N;

    public ConnectMessage(int s_NN, int s_LN, Node s_N) {
        super(s_NN);

        this.s_LN = s_LN;
        this.s_N = s_N;
        this.messageType = CONNECT;
    }

    public int getNodeLevel() {
        return s_LN;
    }

    public Node getNode() {
        return s_N;
    }
}
