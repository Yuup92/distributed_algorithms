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
