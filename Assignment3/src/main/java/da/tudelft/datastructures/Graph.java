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

    public void addLink(int nodeNumber, int neighbourNumber, int maxNodeNumber, HashMap<Node,HashMap<Integer,Integer>> hmapNeighbourhood) {

        if ((nodeNumber < 1)|(nodeNumber > maxNodeNumber)){
            //don't add anything
        }
        else if (hmapNeighbourhood.get(hmapNode.get(nodeNumber)).get(neighbourNumber) != 0) {
            Random random = new Random();
            int rnumber = random.nextInt(10 - 1 + 1) + 1;
            hmapNeighbourhood.get(hmapNode.get(nodeNumber)).put(neighbourNumber,rnumber); //we assign a random weight to the edges
            hmapNeighbourhood.get(hmapNode.get(neighbourNumber)).put(nodeNumber,rnumber);
            hmapStatus.get(hmapNode.get(nodeNumber)).put(neighbourNumber,"?_in_MST"); //we initialize all nodes as not in MST
            hmapStatus.get(hmapNode.get(neighbourNumber)).put(nodeNumber,"?_in_MST"); //everything we change from one side we also change from the other one
        }
    }

    public void createNetwork(int maxNodeNumber) {
        int rows = (int)Math.round(Math.sqrt(maxNodeNumber)); //check if this is doing what it's supposed to
        int columns = maxNodeNumber/rows;
        for (int a = 1; a < maxNodeNumber; a++){
            Node node = new Node();
            hmapNode.put(a, node);
            hmapNeighbourhood.put(node, new HashMap<Integer,Integer>());
            hmapNeighbourhood.get(node).put(a,0);
            hmapStatus.put(node, new HashMap <Integer, String>());
        }

        for (int i = 1; i < maxNodeNumber; i++){

            if ((i-1)%columns == 0){  //if we are on the left border of the grid
                addLink(i,i+1,maxNodeNumber,hmapNeighbourhood); //we keep the one on the right, but not the one on the left
                addLink(i,i+columns,maxNodeNumber,hmapNeighbourhood); //we also keep the one under it
                addLink(i,i-columns,maxNodeNumber,hmapNeighbourhood); //and the one on top of it, and if it's node 1 it will be negative and the other process won't do anything
                addLink(i,i+columns-1,maxNodeNumber,hmapNeighbourhood); //and the diagonal connection up-right
            }
            else if (i%columns ==0){  //if we are on the right border of the grid
                addLink(i,i-1,maxNodeNumber,hmapNeighbourhood);
                addLink(i,i+columns,maxNodeNumber,hmapNeighbourhood); //we also keep the one under it
                addLink(i,i-columns,maxNodeNumber,hmapNeighbourhood);
                addLink(i,i+columns-1,maxNodeNumber,hmapNeighbourhood); //now we keep the diagonal connection down-left
            }
            else {  //center of the grid
                addLink(i,i-1,maxNodeNumber,hmapNeighbourhood);
                addLink(i,i+1,maxNodeNumber,hmapNeighbourhood);
                addLink(i,i+columns-1,maxNodeNumber,hmapNeighbourhood);
                addLink(i,i-columns+1,maxNodeNumber,hmapNeighbourhood);
                addLink(i,i-columns,maxNodeNumber,hmapNeighbourhood);
                addLink(i,i+columns,maxNodeNumber,hmapNeighbourhood);
            }
        }
    }
}
