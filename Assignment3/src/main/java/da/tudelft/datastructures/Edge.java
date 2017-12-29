package da.tudelft.datastructures;

public class Edge {

    private int linkToNode;
    private int weight;
    private String url;

    public Edge(int linkToNode, int weight, String url) {
        this.linkToNode = linkToNode;
        this.weight = weight;
        this.url = url;
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

    public String toString() {
       return "   Linked Node: " + this.linkToNode + "  with weight: " + this.weight + " url: " + this.url;
    }


}
