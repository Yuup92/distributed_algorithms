package da.tudelft.ghs;

import da.tudelft.datastructures.Message;
import da.tudelft.datastructures.Node;
import da.tudelft.datastructures.Graph;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class DA_Gallager_Humblet_Spira extends UnicastRemoteObject implements DA_Gallager_Humblet_Spira_RMI, Runnable {

    private boolean delay = false;
    private Graph graph;

    private int currentClock;
    private ArrayList<Message> mQueue;

    private HashMap<Integer, Node> hmapNode = graph.getHmapNode();
    private HashMap<Node,HashMap<Integer,Integer>> hmapNeighbourhood = graph.getHmapNeighbourhood(); //the second integer is the weights
    private HashMap<Node,HashMap<Integer,String>> hmapStatus = graph.getHmapStatus();

    public DA_Gallager_Humblet_Spira(){
        this.currentClock = 0;

    }

    public void run() {


    }
    public HashMap.Entry<Integer,Integer> minimumWeight(Node node) {
        HashMap<Integer, Integer> hmapWeights = new HashMap<Integer, Integer>();
        hmapWeights = hmapNeighbourhood.get(node);

        HashMap.Entry<Integer, Integer> minEntry = null;

        for (HashMap.Entry<Integer, Integer> entry : hmapWeights.entrySet()) {
            if (minEntry == null || minEntry.getValue() > entry.getValue()) {
                minEntry = entry;
            }
        }
        return minEntry;
    }

    public void receiveMessage(Message message){
        int senderNodeNumber = message.getSenderNodeNumber();
        int receiverNodeNumber = message.getReceiverNodeNumber();
        Node senderNode = new Node();
        Node receiverNode = new Node();
        senderNode = hmapNode.get(senderNodeNumber);
        int weight = hmapNeighbourhood.get(senderNode).get(receiverNodeNumber); //Give me the weight from the sender to the receiver

        if (message.getMessageType() == "connect") {
            if (hmapNode.get(receiverNodeNumber).getNodeStatus() == "sleeping"){
                wakeup(receiverNodeNumber);
            }
            else if (message.getLevel() < hmapNode.get(receiverNodeNumber).getLevel()){
                hmapStatus.get(senderNode).put(receiverNodeNumber,"in_MST");
                sendMessage(receiverNode, senderNode, "initiate", message.getLevel(), weight, receiverNode.getNodeStatus());
                if (receiverNode.getNodeStatus() == "find"){
                    int findCount = receiverNode.getFindCount();
                    findCount = findCount + 1;
                    receiverNode.setFindCount(findCount);
                }
                else {
                    if (edgeStatus == "?_in_MST"){
                        this.mQueue.add(message);
                    }
                    else {
                        sendMessage(receiverNode, senderNode, "initiate", message.getLevel()+1, weight, "find");
                    }

                }
            }

        }
        else if(message.getMessageType() == "initiate"){


        }



    }

    public void sendMessage(int receiverNodeNumber, int senderNodeNumber, String messageType, int Level, int Fragment, int nodeStatus){
        Message m = new Message (this.senderNode, this.receiverNode, this.messageType, this.Level, this.Fragment, this.nodeStatus);
        try{
            receiveMessage(receiverNodeNumber, m);
        }
        catch (NotBoundException | MalformedURLException | RemoteException e) {
            System.out.println("Something went wrong in the sendMessage function for node " + senderNode.getNodeNumber() + ", err: " + e);
            e.printStackTrace();}
    }

    public void wakeup(int nodeNumber){ //we carry the node numbers around and then search the hashmap to find the node
        Node node = new Node();
        node = hmapNode.get(nodeNumber);
        HashMap.Entry<Integer, Integer> minEntry = minimumWeight(node);
        int bestCandidate = minEntry.getKey();
        int bestWeight = minEntry.getValue();
        node.setBestCandidate(bestCandidate);
        node.setBestWeight(bestWeight);
        hmapStatus.get(node).put(bestCandidate,"in_MST");
        node.setLevel(0);
        node.setNodeStatus("found");
        node.setFindCount(0);
        hmapNode.put(nodeNumber,node); //let's update good old nodelist now that we changed stuff
        sendMessage(bestCandidate, nodeNumber,"connect", node.getLevel(), node.getFragment(), node.getNodeStatus());

    }

}
