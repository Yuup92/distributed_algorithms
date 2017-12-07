package da.tudelft.datastructures;


import java.util.ArrayList;

public class Node {


    private int nodeNumber;
    private ArrayList<Integer> nodeLinks;

    private String url;
    private ArrayList<String> urls;

    public Node(int nodeNumber, String url) {
        this.nodeNumber = nodeNumber;
        this.url = url;
        this.urls = new ArrayList<>();
    }

    public int getNodeNumber() {
        return this.nodeNumber;
    }

    public ArrayList<Integer> getNodeLinks() {
        return nodeLinks;
    }

    public String getURL() {
        return this.url;
    }

    /**
     * Function checks the list of current nodeLinks, if that node
     * is not in the list it will add it and return true. If node is already
     * in link than the function will return false.
     * @param link - number of node that is being linked to
     * @return
     */
    public boolean addLink(int link, String url) {

        for (int i = 0; i < nodeLinks.size(); i++) {
            if(nodeLinks.get(i) == link) {
                return false;
            }
        }
        this.nodeLinks.add(link);
        this.urls.add(url);
        return true;
    }


    public boolean checkConnection(Node node) {
        if(node.getNodeNumber() == this.nodeNumber) {
            return false;
        }

        ArrayList<Integer> links = node.getNodeLinks();

        for (int i = 0; i < links.size(); i++) {
            if(this.nodeNumber == links.get(i)) {
                this.addLink(node.getNodeNumber(), node.getURL());
                return true;
            }
        }

        return false;
    }
}
