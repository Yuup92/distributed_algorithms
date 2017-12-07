package da.tudelft.datastructures;


import java.util.ArrayList;

public class Node {


    private int nodeNumber;
    private ArrayList<Integer> nodeLinks;

    public Node(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public int getNodeNumber() {
        return this.nodeNumber;
    }

    public ArrayList<Integer> getNodeLinks() {
        return nodeLinks;
    }

    /**
     * Function checks the list of current nodeLinks, if that node
     * is not in the list it will add it and return true. If node is already
     * in link than the function will return false.
     * @param link - number of node that is being linked to
     * @return
     */
    public boolean addLink(int link) {

        for (int i = 0; i < nodeLinks.size(); i++) {
            if(nodeLinks.get(i) == link) {
                return false;
            }
        }

        this.nodeLinks.add(link);

        
    }
}
