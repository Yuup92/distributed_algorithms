package da.tudelft.bufferMessage;

/**
 * Created by yuupv on 08-Jan-18.
 */
public class ReportMessage extends BufferMessage {

    private int weight;

    public ReportMessage(int s_NN, int weight) {
        super(s_NN);
        this.weight = weight;
        this.messageType = REPORT;
    }

    public int getBestWeight() {
        return this.weight;
    }
}
