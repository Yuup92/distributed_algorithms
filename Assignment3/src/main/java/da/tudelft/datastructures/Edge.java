package da.tudelft.datastructures;

import java.io.Serializable;

public class Edge implements Serializable {

    private int linkToNode;
    private int weight;
    private String url;

    private int edgeState;

    //Constant variables
    private final static int NOT_IN_MST = -1;
    private final static int UNKNOWN_MST = 0;
    private final static int IN_MST = 1;

    public Edge(int linkToNode, int weight, String url) {
        this.linkToNode = linkToNode;
        this.weight = weight;
        this.url = url;

        this.edgeState = UNKNOWN_MST;
    }

    public String toString() {
        return "   Linked Node: " + this.linkToNode + "  with weight: " + this.weight + " url: " + this.url;
    }

    public boolean isUnkownInMST() {
        if(this.edgeState == UNKNOWN_MST) {
            return true;
        }
        return false;
    }

    public void setEdgeStateNOTMST() {
        this.edgeState = NOT_IN_MST;
    }

    public void setEdgeStateUNKNOWNMST() {
        this.edgeState = UNKNOWN_MST;
    }

    public void setEdgeStateINMST() {
        this.edgeState = IN_MST;
    }

    public int getEdgeState() {
        return this.edgeState;
    }

    public String getEdgeStateString() {
        if(edgeState == NOT_IN_MST) {
            return "NOT_IN_MST";
        } else if (edgeState == UNKNOWN_MST) {
            return "?_MST";
        } else if (edgeState == IN_MST) {
            return "IN_MST";
        } else {
            return "ERROR, state was changed to invalid input: " + edgeState;
        }
    }

    public int getLinkToNode() {
        return this.linkToNode;
    }

    public int getWeight() {
        return this.weight;
    }

    public String getUrl() {
        return this.url;
    }

}
