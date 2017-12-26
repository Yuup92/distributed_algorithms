package da.tudelft.datastructures;


import java.util.ArrayList;

public class Node {


    private int nodeNumber;
    private ArrayList<Edge> nodeEdges;

    private String url;
    private ArrayList<String> urls;

    public Node(int nodeNumber, String url) {
        this.nodeNumber = nodeNumber;
        this.url = url;
        this.urls = new ArrayList<>();
        this.nodeEdges = new ArrayList<>();
    }

    public int getNodeNumber() {
        return this.nodeNumber;
    }

    public String getUrl() {
        return this.url;
    }

    public ArrayList<Edge> getNodeEdges() {
        return nodeEdges;
    }

    public String getURL() {
        return this.url;
    }

    /**
     * Function checks the list of current nodeLinks, if that node
     * is not in the list it will add it and return true. If node is already
     * in link than the function will return false.
     * @return
     */
    public boolean addLink(Edge edge) {

        for (int i = 0; i < nodeEdges.size(); i++) {
            if(nodeEdges.get(i).getLinkToNode() == edge.getLinkToNode()) {
                return false;
            }
        }
        this.nodeEdges.add(edge);
        return true;
    }


    public boolean checkConnection(Node node) {
        if(node.getNodeNumber() == this.nodeNumber) {
            return false;
        }

        ArrayList<Edge> nodeEdges = node.getNodeEdges();

        for (int i = 0; i < nodeEdges.size(); i++) {
            if(this.nodeNumber == nodeEdges.get(i).getLinkToNode()) {
                Edge edge = new Edge(node.getNodeNumber(), nodeEdges.get(i).getWeight(), node.getUrl());
                this.addLink(edge);
                return true;
            }
        }

        return false;
    }

    public void showConnection() {

        String list = "\n";
        for (int i = 0; i < this.nodeEdges.size() ; i++) {
            list = list + this.nodeEdges.get(i).toString() + "\n";
        }
        System.out.println("Node: " + this.nodeNumber + ", with url: " + this.url + " is connected to: " + list);
    }
}
