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
    private int nodeID;
    private Graph graph;
    private String url;
    private String[] allUrls;
    private Node node;


    private int currentClock;
    private ArrayList<Message> mQueue;

    private HashMap<Integer, Node> hmapNode = new HashMap<>();
    private HashMap<Node,HashMap<Integer,Integer>> hmapNeighbourhood= new HashMap<>(); //the second integer is the weights
    private HashMap<Node,HashMap<Integer,String>> hmapStatus= new HashMap<>();

    public DA_Gallager_Humblet_Spira(int id, String url, String[] urls, Graph g) throws RemoteException{
        this.url = url;
        this.allUrls = urls;

        this.graph = g;
        this.hmapNode = graph.getHmapNode();
        this.hmapNeighbourhood = graph.getHmapNeighbourhood();

        this.hmapStatus = graph.getHmapStatus();

        this.node = new Node();
        this.node = hmapNode.get(id+1);

        System.out.println("Hi, we are in node " + node.getNodeNumber() + " and it should equal " + hmapNode.get(id+1).getNodeNumber() + " with ID " + (id+1));

        this.currentClock = 0;
    }

    public void run() {

    }

    public void wakeup(int nodeNumber){ //we carry the node numbers around and then search the hashmap to find the node
        Node wakeupNode = new Node();
        wakeupNode = hmapNode.get(nodeNumber);
        System.out.println("Wake up, " + (hmapNode.get(nodeNumber).getNodeStatus() + " node " + nodeNumber));
        HashMap.Entry<Integer, Integer> minEntry = minimumWeight(hmapNode.get(nodeNumber));
        int bestCandidate = minEntry.getKey();
        int bestWeight = minEntry.getValue();
        wakeupNode.setBestCandidate(bestCandidate);
        wakeupNode.setBestWeight(bestWeight);
        hmapStatus.get(wakeupNode).put(bestCandidate,"in_MST");
        wakeupNode.setLevel(0);
        wakeupNode.setNodeStatus("found");
        wakeupNode.setFindCount(0);
        hmapNode.put(nodeNumber,wakeupNode); //let's update good old nodelist now that we changed stuff
        try {System.out.println("Connection attempt from "  + node.getNodeNumber() + " to " + bestCandidate);
            sendMessage(bestCandidate, nodeNumber, "connect", node.getLevel(), node.getFragment(), node.getNodeStatus());
        }
        catch (RemoteException e){
            System.out.println("Something went wrong in the sendMessage function for node " + nodeNumber + ", err: " + e);
            e.printStackTrace();
        }
    }

    public HashMap.Entry<Integer,Integer> minimumWeight(Node node) {
        Node nodeMin = new Node();
        nodeMin = node;
        HashMap<Integer, Integer> hmapWeights = new HashMap<Integer, Integer>();
        hmapWeights = hmapNeighbourhood.get(nodeMin);
        System.out.println("hmapNeigbour: "+ hmapNeighbourhood.get(nodeMin));
        System.out.println("hmapNeighbourStatus: " + hmapStatus.get(nodeMin));

        HashMap.Entry<Integer, Integer> minEntry = null;

        for (HashMap.Entry<Integer, Integer> entry : hmapWeights.entrySet()) {
            if ((minEntry == null) || (minEntry.getValue() > entry.getValue())) {
                minEntry = entry;
            }
        }
        return minEntry;
    }

    public synchronized void receiveMessage(Message msg) throws RemoteException{
        System.out.println("A message " + msg.getMessageType() + " from " + msg.getSenderNodeNumber() + " to " + msg.getReceiverNodeNumber() + " we are in node " + node.getNodeNumber());
        Message message = msg;
        int senderNodeNumber = message.getSenderNodeNumber();
        int receiverNodeNumber = message.getReceiverNodeNumber();
        int Level = message.getLevel();
        Node senderNode = new Node();
        Node receiverNode = new Node();
        senderNode = hmapNode.get(senderNodeNumber);
        receiverNode = hmapNode.get(receiverNodeNumber);

        if (message.getMessageType().compareTo("connect") == 0) {
            System.out.println("Entering the connect subcase, the node is " + receiverNodeNumber + " and it is " + hmapNode.get(receiverNodeNumber).getNodeStatus());
            if (hmapNode.get(receiverNodeNumber).getNodeStatus() == "sleeping"){
                System.out.println("Let us wake up " + receiverNode.getNodeStatus() + " node " + receiverNodeNumber);
                wakeup(receiverNodeNumber);
            }
            else if (message.getLevel() < hmapNode.get(receiverNodeNumber).getLevel()){
                hmapStatus.get(receiverNode).put(senderNodeNumber,"in_MST");
                System.out.println("Node " + senderNodeNumber + " sent " + message.getMessageType() + " to node " + receiverNodeNumber + "and is now" + hmapStatus.get(receiverNode).get(senderNodeNumber));
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
            }
            else {
                System.out.println("Receiver node is " + receiverNodeNumber + " with status " + receiverNode.getNodeStatus() + " and sender node is " + senderNodeNumber + " with status " + senderNode.getNodeStatus());
                if (hmapStatus.get(receiverNode).get(senderNodeNumber) == "?_in_MST"){ //if the sender is in the neighbourhood of the receiver
                    System.out.println("Connect, ?_in_MST case");
                    this.mQueue.add(message);
                }
                else {
                    System.out.println("Connect, absorb");
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
        else if(message.getMessageType().compareTo("initiate") == 0){
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
        else if (message.getMessageType().compareTo("reject") == 0){
            if (hmapStatus.get(receiverNode).get(senderNodeNumber) == "?_in_MST") {
                hmapStatus.get(receiverNode).put(senderNodeNumber,"not_in_MST");
            }
            test(receiverNodeNumber);
        }
        else if (message.getMessageType().compareTo("accept") == 0){
            receiverNode.setTestCandidate(0); //it's a zero so there is no test candidate
            if (hmapNeighbourhood.get(senderNode).get(receiverNodeNumber) < receiverNode.getBestWeight()){
                receiverNode.setBestWeight(hmapNeighbourhood.get(senderNode).get(receiverNodeNumber));
                receiverNode.setBestCandidate(senderNodeNumber);
                hmapNode.put(receiverNodeNumber,receiverNode);
            }
        }
        else if (message.getMessageType().compareTo("report") == 0){
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
                            System.out.println("You got it");
                            for (HashMap.Entry<Integer, Node> entry : hmapNode.entrySet()) {
                                System.out.println("We are looking at node: " + entry.getKey());
                                HashMap<Integer,Integer> hmapWeights = new HashMap<Integer,Integer>();
                                hmapWeights = hmapNeighbourhood.get(entry.getValue());
                                for (HashMap.Entry<Integer, Integer> entry2 : hmapWeights.entrySet()){
                                    System.out.println("And it is connected to: " + entry2.getKey() + "with weight " + entry2.getValue() + "and status " + hmapStatus.get(entry.getValue()).get(entry2.getKey()));
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
        else if (message.getMessageType().compareTo("change_root") == 0){
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

            try{
                sendMessage(node.getEdgeTowardsCore(), nodeNumber,"report", node.getLevel(), node.getFragment(), node.getNodeStatus());
            }
            catch (RemoteException  e) {
                System.out.println("Something went wrong in the sendMessage function for node " + nodeNumber + ", err: " + e);
                e.printStackTrace();}
        }
    }

    public void sendMessage(int receiverNodeNumber, int senderNodeNumber, String messageType, int Level, int Fragment, String nodeStatus) throws RemoteException{
        System.out.println("sendMessage " + messageType + " from " + senderNodeNumber + " to " + receiverNodeNumber);
        Message m = new Message (receiverNodeNumber, senderNodeNumber, messageType, Level, Fragment, nodeStatus);

        try{
            DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) Naming.lookup(this.allUrls[receiverNodeNumber-1]);
            System.out.println("sendMessage, url: " + this.allUrls[receiverNodeNumber-1]);
            receiver.receiveMessage(m);
        }
        catch (RemoteException | MalformedURLException | NotBoundException e) {
            System.out.println("Something went wrong in the sendMessage function for node " + receiverNodeNumber + ", err: " + e);
            e.printStackTrace();}
    }

    public void printNetwork() {
        for (HashMap.Entry<Integer, Node> entry : hmapNode.entrySet()) {
            System.out.println("We are looking at node: " + entry.getKey());
            HashMap<Integer,Integer> hmapWeights = new HashMap<Integer,Integer>();
            hmapWeights = hmapNeighbourhood.get(entry.getValue());
            for (HashMap.Entry<Integer, Integer> entry2 : hmapWeights.entrySet()){
                System.out.println("And it is connected to: " + entry2.getKey() + "with weight" + entry2.getValue());
            }
        }
    }


}
