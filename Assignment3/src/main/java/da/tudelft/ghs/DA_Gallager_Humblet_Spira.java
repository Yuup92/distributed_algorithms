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

    public DA_Gallager_Humblet_Spira() throws RemoteException{
        this.currentClock = 0;
    }

    public void run() {

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
        try {
            sendMessage(bestCandidate, nodeNumber, "connect", node.getLevel(), node.getFragment(), node.getNodeStatus());
        }
        catch (RemoteException e){
            System.out.println("Something went wrong in the sendMessage function for node " + nodeNumber + ", err: " + e);
            e.printStackTrace();
        }
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

    public void receiveMessage(Message msg) throws RemoteException{
        Message message = msg;
        int senderNodeNumber = message.getSenderNodeNumber();
        int receiverNodeNumber = message.getReceiverNodeNumber();
        int Level = message.getLevel();
        Node senderNode = new Node();
        Node receiverNode = new Node();
        senderNode = hmapNode.get(senderNodeNumber);

        if (message.getMessageType() == "connect") {
            if (hmapNode.get(receiverNodeNumber).getNodeStatus() == "sleeping"){
                wakeup(receiverNodeNumber);
            }
            else if (message.getLevel() < hmapNode.get(receiverNodeNumber).getLevel()){
                hmapStatus.get(senderNode).put(receiverNodeNumber,"in_MST");
                try{
                    sendMessage(senderNodeNumber, receiverNodeNumber,"initiate", receiverNode.getLevel(), hmapNeighbourhood.get(senderNode).get(receiverNodeNumber), receiverNode.getNodeStatus());
                }
                catch (RemoteException e) {
                    System.out.println("Something went wrong in the sendMessage function for node " + receiverNodeNumber + ", err: " + e);
                    e.printStackTrace();
                }
                if (receiverNode.getNodeStatus() == "find"){
                    int findCount = receiverNode.getFindCount();
                    findCount = findCount + 1;
                    receiverNode.setFindCount(findCount);
                    hmapNode.put(receiverNodeNumber,receiverNode);
                }
                else {
                    if (hmapStatus.get(receiverNode).get(senderNodeNumber) == "?_in_MST"){ //if the sender is in the neighbourhood of the receiver
                        this.mQueue.add(message);
                    }
                    else {
                        try {
                            sendMessage(senderNodeNumber, receiverNodeNumber, "initiate", receiverNode.getLevel()+1, hmapNeighbourhood.get(senderNode).get(receiverNodeNumber), "find");
                        }
                        catch (RemoteException e) {
                            System.out.println("Something went wrong in the sendMessage function for node " + receiverNodeNumber + ", err: " + e);
                            e.printStackTrace();
                        }
                    }

                }
            }

        }
        else if(message.getMessageType() == "initiate"){
            receiverNode.setLevel(message.getLevel());
            receiverNode.setFragment(message.getFragment());
            receiverNode.setNodeStatus(message.getStatus());
            receiverNode.setEdgeTowardsCore(message.getSenderNodeNumber());
            receiverNode.setBestCandidate(0);
            receiverNode.setBestWeight(1000); //much greater than 10, thus infinite for us
            hmapNode.put(receiverNodeNumber,receiverNode); //update
            HashMap<Integer, Integer> hmapWeights = new HashMap<Integer, Integer>();
            hmapWeights = hmapNeighbourhood.get(receiverNode);

            for (HashMap.Entry<Integer, Integer> entry : hmapWeights.entrySet()) {
                if (hmapStatus.get(receiverNode).get(entry.getKey()) == "in_MST") {
                    try {
                        sendMessage(senderNodeNumber, receiverNodeNumber, "initiate", message.getLevel(), message.getFragment(), receiverNode.getNodeStatus());
                    }
                    catch (RemoteException e) {
                        System.out.println("Something went wrong in the sendMessage function for node " + senderNodeNumber + ", err: " + e);
                        e.printStackTrace();
                    }
                    if(receiverNode.getNodeStatus()=="find"){
                        receiverNode.setFindCount(receiverNode.getFindCount()+1);
                        hmapNode.put(receiverNodeNumber,receiverNode); //update
                    }

                }
                if (receiverNode.getNodeStatus() == "find"){
                    test(receiverNodeNumber);
                }
            }
            if(receiverNode.getNodeStatus()=="find") {
                test(receiverNodeNumber);
            }

        }
        else if (message.getMessageType() == "test"){
            if (receiverNode.getNodeStatus()=="sleeping"){
                wakeup(receiverNodeNumber);
            }
            if (message.getLevel()>receiverNode.getLevel()){
                this.mQueue.add(message);
            }
            else {
                if (message.getFragment() != receiverNode.getFragment()){
                    try{
                        sendMessage(senderNodeNumber,receiverNodeNumber,"accept",message.getLevel(),message.getFragment(),receiverNode.getNodeStatus());
                    }
                    catch (RemoteException e) {
                        System.out.println("Something went wrong in the sendMessage function for node " + receiverNodeNumber + ", err: " + e);
                        e.printStackTrace();}
                }
                else {
                    if (hmapStatus.get(receiverNode).get(senderNodeNumber) == "?_in_MST") {
                        hmapStatus.get(receiverNode).put(senderNodeNumber,"not_in_MST");
                    }
                    if (receiverNode.getTestCandidate() != senderNodeNumber){
                        try{
                            sendMessage(senderNodeNumber,receiverNodeNumber,"reject",message.getLevel(),message.getFragment(),receiverNode.getNodeStatus());
                        }
                        catch (RemoteException e) {
                            System.out.println("Something went wrong in the sendMessage function for node " + receiverNodeNumber + ", err: " + e);
                            e.printStackTrace();}
                    }
                    else {
                        test(receiverNodeNumber);
                    }
                }
            }
        }
        else if (message.getMessageType() == "reject"){
            if (hmapStatus.get(receiverNode).get(senderNodeNumber) == "?_in_MST") {
                hmapStatus.get(receiverNode).put(senderNodeNumber,"not_in_MST");
            }
            test(receiverNodeNumber);
        }
        else if (message.getMessageType() == "accept"){
            receiverNode.setTestCandidate(0); //it's a zero so there is no test candidate
            if (hmapNeighbourhood.get(senderNode).get(receiverNodeNumber) < receiverNode.getBestWeight()){
                receiverNode.setBestWeight(hmapNeighbourhood.get(senderNode).get(receiverNodeNumber));
                receiverNode.setBestCandidate(senderNodeNumber);
                hmapNode.put(receiverNodeNumber,receiverNode);
            }
        }
        else if (message.getMessageType() == "report"){
            if (receiverNode.getEdgeTowardsCore() != senderNodeNumber) {
                receiverNode.setFindCount(receiverNode.getFindCount()+1);
                if (hmapNeighbourhood.get(receiverNode).get(senderNodeNumber) < receiverNode.getBestWeight()){
                    receiverNode.setBestWeight(hmapNeighbourhood.get(receiverNode).get(senderNodeNumber));
                    receiverNode.setBestCandidate(senderNodeNumber);
                }
                report(receiverNodeNumber);
            }
            else {
                if (receiverNode.getNodeStatus() == "find"){
                    this.mQueue.add(message);
                }
                else {
                    if (hmapNeighbourhood.get(receiverNode).get(senderNodeNumber) > receiverNode.getBestWeight()){
                        change_root(receiverNodeNumber);
                    }
                    else {
                        if (hmapNeighbourhood.get(receiverNode).get(senderNodeNumber) == receiverNode.getBestWeight()){
                            return;
                        }
                    }
                }
            }
        }
        else if (message.getMessageType() == "change_root"){
            change_root(receiverNodeNumber);
        }
    }

    public void change_root(int receiverNodeNumber){
        Node receiverNode = hmapNode.get(receiverNodeNumber);
        if (hmapStatus.get(receiverNode).get(receiverNode.getBestCandidate()) == "in_MST"){
            try{
                sendMessage(receiverNodeNumber,receiverNode.getBestCandidate(),"change_root",receiverNode.getLevel(),receiverNode.getFragment(),receiverNode.getNodeStatus());
            }
            catch (RemoteException e) {
                System.out.println("Something went wrong in the sendMessage function for node " + receiverNodeNumber + ", err: " + e);
                e.printStackTrace();}
        }
        else {
            try{
                sendMessage(receiverNodeNumber,receiverNode.getBestCandidate(),"connect",receiverNode.getLevel(),receiverNode.getFragment(),receiverNode.getNodeStatus());
            }
            catch (RemoteException e) {
                System.out.println("Something went wrong in the sendMessage function for node " + receiverNodeNumber + ", err: " + e);
                e.printStackTrace();
            }
            hmapStatus.get(receiverNode).put(receiverNodeNumber,"in_MST");
        }
    }

    public void test(int nodeNumber){
        int receiverNodeNumber = nodeNumber;
        Node receiverNode = new Node();
        receiverNode = hmapNode.get(nodeNumber);
        HashMap<Integer, Integer> hmapWeights = new HashMap<Integer, Integer>();
        hmapWeights = hmapNeighbourhood.get(receiverNode);
        int nodeNumberToTest = 0;
        HashMap.Entry<Integer, Integer> minEntry = null;
        for (HashMap.Entry<Integer, Integer> entry : hmapWeights.entrySet()) {
            if (minEntry == null || ((minEntry.getValue() > entry.getValue())&&(hmapStatus.get(receiverNode).get(entry.getKey()) == "?_in_MST") )){
                minEntry = entry;
                nodeNumberToTest = minEntry.getKey();
            }
            if (minEntry != null){ //if there was any adjacent edge in unknown state
                try{
                    sendMessage(nodeNumberToTest,receiverNodeNumber,"test",receiverNode.getLevel(),receiverNode.getFragment(),receiverNode.getNodeStatus());
                }
                catch (RemoteException e) {
                    System.out.println("Something went wrong in the sendMessage function for node " + receiverNodeNumber + ", err: " + e);
                    e.printStackTrace();}
            }
            else {
                report(receiverNodeNumber);
            }
        }
    }

    public void report(int nodeNumber){
        if ((hmapNode.get(nodeNumber).getFindCount() == 0)&&(hmapNode.get(nodeNumber).getTestCandidate() != 0)){
            Node node = new Node();
            node.setNodeStatus("found");
            hmapNode.put(nodeNumber,node);
        }
    }

    public void sendMessage(int receiverNodeNumber, int senderNodeNumber, String messageType, int Level, int Fragment, String nodeStatus) throws RemoteException{
        Message m = new Message (receiverNodeNumber, senderNodeNumber, messageType, Level, Fragment, nodeStatus);
        try{
            receiveMessage(m);
        }
        catch (RemoteException e) {
            System.out.println("Something went wrong in the sendMessage function for node " + receiverNodeNumber + ", err: " + e);
            e.printStackTrace();}
    }


}
