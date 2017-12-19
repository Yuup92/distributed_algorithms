package da.tudelft.datastructures;

import java.util.*;
import java.lang.Math;
import java.util.Random;

public class Graph {

    private HashMap<Integer, Node> hmapNode = new HashMap<Integer, Node>(); //This way I can access nodes by their number
    private HashMap<Node,HashMap<Integer,Integer>> hmapNeighbourhood = new HashMap<Node,HashMap<Integer,Integer>>(); //this way I link nodes to their neighbourhood
    private HashMap<Node, HashMap<Integer,String>> hmapStatus = new HashMap<Node, HashMap<Integer,String>>();

    public HashMap<Integer, Node> getHmapNode(){
        return this.hmapNode;
    }

    public HashMap<Node, HashMap<Integer,Integer>> getHmapNeighbourhood() {
        return this.hmapNeighbourhood;
    }

    public HashMap<Node, HashMap<Integer,String>> getHmapStatus(){
        return this.hmapStatus;
    }

    public boolean addLink(int nodeNumber, int maxNodeNumber, Node node, HashMap<Node,HashMap<Integer,Integer>> hmapNeighbourhood) {

        if ((nodeNumber < 1)|(nodeNumber > maxNodeNumber)){
            //don't add anything
            return false;
        }
        else {
            Random random = new Random();
            int rnumber = random.nextInt(10 - 1 + 1) + 1;
            hmapNeighbourhood.get(node).put(nodeNumber,rnumber); //we assign a random weight to the edges
            hmapStatus.get(node).put(nodeNumber,"not_in_MST"); //we initialize all nodes as not in MST
            return true;
        }
    }

    public void createNetwork(int maxNodeNumber) {
        int rows = (int)Math.round(Math.sqrt(maxNodeNumber)); //check if this is doing what it's supposed to
        int columns = maxNodeNumber/rows;
        for (int i = 1; i < maxNodeNumber; i++){
            Node node = new Node();
            hmapNode.put(i, node);
            hmapNeighbourhood.put(node, new HashMap<Integer,Integer>());
            hmapStatus.put(node, new HashMap <Integer, String>());
            if ((i-1)%columns == 0){  //if we are on the left border of the grid
                addLink(i+1,maxNodeNumber,node,hmapNeighbourhood); //we keep the one on the right, but not the one on the left
                addLink(i+columns,maxNodeNumber,node,hmapNeighbourhood); //we also keep the one under it
                addLink(i-columns,maxNodeNumber,node,hmapNeighbourhood); //and the one on top of it, and if it's node 1 it will be negative and the other process won't do anything
                addLink(i+columns-1,maxNodeNumber,node,hmapNeighbourhood); //and the diagonal connection up-right
            }
            else if (i%columns ==0){  //if we are on the right border of the grid
                addLink(i-1,maxNodeNumber,node,hmapNeighbourhood);
                addLink(i+columns,maxNodeNumber,node,hmapNeighbourhood); //we also keep the one under it
                addLink(i-columns,maxNodeNumber,node,hmapNeighbourhood);
                addLink(i+columns-1,maxNodeNumber,node,hmapNeighbourhood); //now we keep the diagonal connection down-left
            }
            else {
                addLink(i-1,maxNodeNumber,node,hmapNeighbourhood);
                addLink(i+1,maxNodeNumber,node,hmapNeighbourhood);
                addLink(i+columns-1,maxNodeNumber,node,hmapNeighbourhood);
                addLink(i-columns+1,maxNodeNumber,node,hmapNeighbourhood);
                addLink(i-columns,maxNodeNumber,node,hmapNeighbourhood);
                addLink(i+columns,maxNodeNumber,node,hmapNeighbourhood);
            }
        }
    }
}
