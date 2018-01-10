package da.tudelft.datastructures;


import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable{

    private int nodeNumber;
    private ArrayList<Edge> nodeEdges;

    private String url;
    private ArrayList<String> urls;

    private int nodeState;

    //Data node must be aware of
    private int nameFragement;
    private int levelFragement;
    private int numberReportMessages;
    private int findCount;

    private Edge inBranch = null;
    private Edge coreEdge = null;
    private Edge bestEdge = null;
    private Edge currentTestEdge= null;

    //Constant variables
    private final static int SLEEPING = 0;
    private final static int FIND = 1;
    private final static int FOUND = 2;

    public Node(int nodeNumber, String url) {
        this.nodeNumber = nodeNumber;
        this.url = url;
        this.urls = new ArrayList<>();
        this.nodeEdges = new ArrayList<>();

        this.nodeState = SLEEPING;
        this.levelFragement = -1;
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

        updateBestMOE();

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
                return this.addLink(edge);
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

    public void updateBestMOE() {

        int bestWeight = (this.bestEdge != null) ? this.bestEdge.getWeight() : Integer.MAX_VALUE;

        Edge testEdge;

        for (int i = 0; i < nodeEdges.size(); i++) {
            testEdge = this.nodeEdges.get(i);
            if(this.bestEdge == null || this.bestEdge.getLinkToNode() != testEdge.getLinkToNode() ) {
                if(bestWeight > testEdge.getWeight()) {
                    this.bestEdge = testEdge;
                    bestWeight = testEdge.getWeight();
                }
            }
        }

    }

    public void resetBestEdge() {
        this.bestEdge = null;

    }

    public void findNameFragment(Node node) {

        if(this.nodeNumber < node.getNodeNumber()) {
            setNameFragment(node.getNodeNumber());
        } else {
            setNameFragment(this.nodeNumber);
        }

    }

    public boolean checkEdgesNotInMST() {

        boolean test = false;
        int bestWeight = Integer.MAX_VALUE;
        this.currentTestEdge = null;

        for (int i = 0; i < this.nodeEdges.size() ; i++) {
            if( this.nodeEdges.get(i).isUnkownInMST() && bestWeight > this.nodeEdges.get(i).getWeight()) {
                this.currentTestEdge = this.nodeEdges.get(i);
                test = true;
            }
        }

        return test;
    }

    /**
     *
     */

    public void setInBranch(int nodeNumber) {



        for (int i = 0; i < this.nodeEdges.size(); i++) {
            if(this.nodeEdges.get(i).getLinkToNode() == nodeNumber) {
                this.inBranch = this.nodeEdges.get(i);
            }
        }

        System.out.println(this.nodeNumber + " has an inbranch. The branch node is: " + this.inBranch.getLinkToNode());

    }

    public void setNodeState(int state) {
        this.nodeState = state;
    }

    public void setNodeStateSLEEPING() {
        this.nodeState = SLEEPING;
    }

    public void setNodeStateFOUND() {
        this.nodeState = FOUND;
    }

    public void setNodeStateFIND() {
        this.nodeState = FIND;
    }

    public void setLevelFragment(int lvl) {
        this.levelFragement = lvl;
    }

    public void setNameFragment (int name) {
        this.nameFragement = name;
    }

    public void setFindCount(int num) {
        this.findCount = num;
    }

    public void setCurrentTestEdge(Edge edge) {
        this.currentTestEdge = edge;
    }

    public void setBestEdge(Edge edge) {
        this.bestEdge = edge;
    }

    /**
     * All get functions should be placed below
     */
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

    public int getNameFragement() {
        return this.nameFragement;
    }

    public int getLevelFragement() {
        return this.levelFragement;
    }

    public int getNumberReportMessages() {
        return this.numberReportMessages;
    }

    public Edge getCoreEdge() {
        return this.coreEdge;
    }

    public int getWeightBestMOE() {
        return this.bestEdge.getWeight();
    }

    public Edge getCurrentTestEdge() {
        return this.currentTestEdge;
    }

    public int getNodeState() {
        return this.nodeState;
    }

    public Edge getBestEdge() {
        return this.bestEdge;
    }

    public Edge getEdgeWithConnection(int nodeNumber) {

        for (int i = 0; i < this.nodeEdges.size(); i++) {
            if(nodeEdges.get(i).getLinkToNode() == nodeNumber) {
                return nodeEdges.get(i);
            }
        }
        return null;
    }

    public Edge getInBranch() {
        return this.inBranch;
    }
}
