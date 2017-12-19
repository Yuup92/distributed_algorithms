package da.tudelft.datastructures;


import java.util.ArrayList;
import java.rmi.Naming;
import java.util.*;


public class Node {


    private int nodeNumber;
    //private ArrayList<Integer> nodeLinks;
    private int Fragment = nodeNumber; //at the beginning the fragment name is just the node number
    private int Level = 0; //at the beginning the level is 0
    private int reportMessagesExpected; //number of report mesages it still expects
    private int bestCandidate; //the best candidate for MOE the node knows about (node number)
    private int bestWeight; //the weight of the best candidate
    private String nodeStatus = "sleeping";
    private int findCount;
    private int edgeTowardsCore;
    private int testCandidate;


    public Node() {
        this.nodeNumber = nodeNumber;
    }

    public int getNodeNumber() {
        return this.nodeNumber;
    }

    public int getFragment(){
        return Fragment;
    }

    public void setFragment(int Fragment){
        this.Fragment = Fragment;
    }

    public int getLevel(){
        return this.Level;
    }

    public void setLevel(int Level){
        this.Level = Level;
    }

    public int getReportMessagesExpected(){
        return reportMessagesExpected;
    }

    public int getBestCandidate(){
        return bestCandidate;
    }

    public void setBestCandidate(int bestCandidate){
        this.bestCandidate = bestCandidate;
    }

    public int getBestWeight(){
        return bestWeight;
    }

    public void setBestWeight(int bestWeight){
        this.bestWeight = bestWeight;
    }

    public String getNodeStatus(){
        return this.nodeStatus;
    }

    public void setNodeStatus(String nodeStatus){
        this.nodeStatus = nodeStatus;
    }

    public int getFindCount(){
        return findCount;
    }

    public void setFindCount(int findCount){
        this.findCount = findCount;
    }

    public int getEdgeTowardsCore() {
        return edgeTowardsCore;
    }

    public void setEdgeTowardsCore(int edgeTowardsCore) {
        this.edgeTowardsCore = edgeTowardsCore;
    }

    public int getTestCandidate() {
        return testCandidate;
    }

    public void setTestCandidate(int testCandidate) {
        this.testCandidate = testCandidate;
    }

    /**
     * Function checks the list of current nodeLinks, if that node
     * is not in the list it will add it and return true. If node is already
     * in link than the function will return false.
     * @param link - number of node that is being linked to
     * @return
     */



}