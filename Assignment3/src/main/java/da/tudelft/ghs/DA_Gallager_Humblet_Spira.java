package da.tudelft.ghs;

import da.tudelft.datastructures.Edge;
import da.tudelft.datastructures.Node;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class DA_Gallager_Humblet_Spira extends UnicastRemoteObject
        implements DA_Gallager_Humblet_Spira_RMI, Runnable, Serializable {

    private int processID;
    private String url;

    private Node node;
    private int findCount;

    //Constant variables
    private final static int SLEEPING = 0;
    private final static int FIND = 1;
    private final static int FOUND = 2;

    private final static int NOT_IN_MST = -1;  //not_MST
    private final static int UNKNOWN_MST = 0;  //?_MST
    private final static int IN_MST = 1;       //in_MST


    public DA_Gallager_Humblet_Spira(int ID, String url) throws RemoteException{
        this.processID = ID;
        this.url = url;

        this.node = null;

        this.findCount = 0;
    }

    public void addNode(Node node){
        this.node = node;
    }

    public void wakeUp() {
        Edge edge = this.node.getBestEdge();
        edge.setEdgeStateINMST();

        this.node.setLevelFragment(0);
        this.node.setNodeStateFOUND();
        this.node.setFindCount(0);

        //This function system.out's
        wakeUpMessage(edge);

        sendConnect(edge.getUrl(), this.node.getLevelFragement());
    }

    @Override
    public void sendConnect(String url, int levelFragment) {

        try{
            DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) Naming.lookup(url);
            receiver.receiveConnect(levelFragment, this.node);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            System.out.println("For process: " + processID + " an error occured sending CONNECT, error: " + e);
            e.printStackTrace();
        }

    }

    @Override
    public void receiveConnect(int levelFragmentSender, Node senderNode) throws RemoteException {

        if (this.node.getNodeState() == SLEEPING) {
            wakeUp();
        }

        Edge edge = this.node.getEdgeWithConnection(senderNode.getNodeNumber());


        if (levelFragmentSender < this.node.getLevelFragement()) {
            edge.setEdgeStateINMST();
            sendInitiate(edge.getUrl(), this.node.getLevelFragement(), this.node.getNameFragement(), this.node.getNodeState(), this.node.getNodeNumber());
            if (this.node.getNodeState() == FIND) {
                this.findCount++;
            }
        } else {
            if (edge.getEdgeState() == UNKNOWN_MST) {
                //TODO add message to message queue
            } else {
                sendInitiate(edge.getUrl(), this.node.getLevelFragement(), this.node.getNameFragement(), FIND, this.node.getNodeNumber());
            }
        }

    }

    @Override
    public void sendInitiate(String url, int nodeLevel, int fragmentName, int nodeState, int nodeNumber) {

        try{
            DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) Naming.lookup(url);
            receiver.receiveInitiate(nodeLevel, fragmentName, nodeState, nodeNumber);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            System.out.println("For process: " + processID + " an error occured sending INITIATE, error: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void receiveInitiate(int s_nodeLevel, int s_fragmentName, int s_nodeState, int s_nodeNumber) throws RemoteException {

        this.node.setLevelFragment(s_nodeLevel);
        this.node.setNameFragment(s_fragmentName);
        this.node.setNodeState(s_nodeState);

        //in-branch??
        this.node.resetBestEdge();

        ArrayList<Edge> listEdges = this.node.getNodeEdges();

        for (int i = 0; i < listEdges.size() ; i++) {
            Edge edge = listEdges.get(i);
            if(edge.getLinkToNode() != s_nodeNumber && edge.getEdgeState() == IN_MST) {
                sendInitiate(edge.getUrl(), s_nodeLevel, s_fragmentName, s_nodeState, this.node.getNodeNumber());
            }
            if(this.node.getNodeState() == FIND) {
                this.findCount++;
            }
        }
        if(this.node.getNodeState() == FIND) {
            //test()
        }

        System.out.println("Received an Initiate at process: " + processID);
    }

    @Override
    public void sendMessage(String url, String message) {

    }

    @Override
    public synchronized void receive(String message) {
        System.out.println();
    }


    public void run() {

    }


    /**
     * Created Messages to help debug and see what is going on in alg
     */

    public void wakeUpMessage(Edge edge) {
        System.out.println("This node: " + node.getNodeNumber() +
                "\n best edge is linked to: " + edge.getLinkToNode() +
                "\n the weight is: " + edge.getWeight() +
                "\n the state of the edge is: " + edge.getEdgeState());
    }
}
