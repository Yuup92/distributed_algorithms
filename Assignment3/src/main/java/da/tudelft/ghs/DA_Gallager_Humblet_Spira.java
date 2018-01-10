package da.tudelft.ghs;

import da.tudelft.bufferMessage.*;
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
    private String _url;

    private Node node;
    private int findCount;

    private ArrayList<BufferMessage> bufferMessages;

    //Constant variables
    private final static int SLEEPING = 0;
    private final static int FIND = 1;
    private final static int FOUND = 2;

    private final static int NOT_IN_MST = -1;  //not_MST
    private final static int UNKNOWN_MST = 0;  //?_MST
    private final static int IN_MST = 1;       //in_MST

    private final static int CONNECT = 0;
    private final static int REPORT = 1;
    private final static int TEST = 2;

    private final static boolean PRINT_BUFFER = false;


    public DA_Gallager_Humblet_Spira(int ID, String url) throws RemoteException{
        this.processID = ID;
        this._url = url;

        this.node = null;
        this.findCount = 0;

        this.bufferMessages = new ArrayList<>();
    }

    public void addNode(Node node){
        this.node = node;
    }

    public void wakeUp() {

        System.out.println(this.processID + " Node: has received WAKEUP");

        Edge edge = this.node.getBestEdge();
        edge.setEdgeStateINMST();

        this.node.setLevelFragment(0);
        this.node.setNodeStateFOUND();
        this.node.setFindCount(0);

        new Thread(new BufferChecker(this)).start();

        //This function system.out's
        //wakeUpMessage(edge);



        sendConnect(edge.getUrl(), this.node.getLevelFragement());
    }

    @Override
    public void sendConnect(String url, int levelFragment) {

        ConnectMessage cM = new ConnectMessage(this.node.getNodeNumber(), this.node.getLevelFragement(), this.node);

        try{
            DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) Naming.lookup(url);
            receiver.receiveConnect(cM);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            System.out.println("For process: " + processID + " an error occured sending CONNECT, error: " + e);
            e.printStackTrace();
        }

    }

    //TODO add message to queue
    //TODO add a way to read the buffer and check the buffer
    @Override
    public void receiveConnect(ConnectMessage cM)  {
        System.out.println(processID + " Node: has received CONNECT message from node: " + cM.getNode().getNodeNumber());

        wakeUpNode();

        Node senderNode = cM.getNode();
        int levelFragmentSender = cM.getNodeLevel();


        Edge edge = this.node.getEdgeWithConnection(senderNode.getNodeNumber());


        if (levelFragmentSender < this.node.getLevelFragement()) {
            edge.setEdgeStateINMST();

            if(this.node.getNameFragement() < 0) {
                this.node.findNameFragment(senderNode);
            }

            sendInitiate(edge.getUrl(), this.node.getLevelFragement(), this.node.getNameFragement(), this.node.getNodeState(), this.node.getNodeNumber());
            if (this.node.getNodeState() == FIND) {

                this.findCount++;
            }
        } else {
            if (edge.getEdgeState() == UNKNOWN_MST) {
                this.bufferMessages.add(cM);
                return;
            } else {
                this.node.setLevelFragment( this.node.getLevelFragement() + 1  );
                sendInitiate(edge.getUrl(), this.node.getLevelFragement(), this.node.getNameFragement(), FIND, this.node.getNodeNumber());
            }
        }

        checkBuffer(CONNECT, PRINT_BUFFER);

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
        System.out.println(processID + " Node: has received a INITIATE message from node: " + s_nodeNumber);

        this.node.setLevelFragment(s_nodeLevel);
        this.node.setNameFragment(s_fragmentName);
        this.node.setNodeState(s_nodeState);
        this.node.setInBranch(s_nodeNumber);

        this.node.resetBestEdge();

        ArrayList<Edge> listEdges = this.node.getNodeEdges();

        for (int i = 0; i < listEdges.size() ; i++) {
            Edge edge = listEdges.get(i);
            if(edge.getLinkToNode() != s_nodeNumber && edge.getEdgeState() == IN_MST) {
                System.out.println();
                sendInitiate(edge.getUrl(), s_nodeLevel, s_fragmentName, s_nodeState, this.node.getNodeNumber());
            }
            if(this.node.getNodeState() == FIND) {
                this.findCount++;
            }
        }
        if(this.node.getNodeState() == FIND) {
            sendTest();
        }


    }

    @Override
    public void sendTest() {

        if( this.node.checkEdgesNotInMST()) {
            String url = this.node.getCurrentTestEdge().getUrl();

            TestMessage tM = new TestMessage(this.node.getNodeNumber(),
                                                this.node.getLevelFragement(),
                                                this.node.getNameFragement());

            try{
                DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) Naming.lookup(url);
                receiver.receiveTest(tM);
            } catch (RemoteException | NotBoundException | MalformedURLException e) {
                System.out.println("For process: " + processID + " an error occured sending INITIATE, error: " + e);
                e.printStackTrace();
            }
        } else {
            this.node.setCurrentTestEdge(null);
            sendReport();
        }
    }

    @Override
    public void receiveTest(TestMessage tM) {

        System.out.println(processID + " Node: has received TEST message from node: " + tM.getNodeNumber());

        int s_NN = tM.getNodeNumber();
        int s_LN = tM.getNodeLevel();
        int s_FN = tM.getFragementName();


        wakeUpNode();

        Edge edge = this.node.getEdgeWithConnection(s_NN);

        if( s_LN > this.node.getLevelFragement() ) {
            System.out.println(processID + " Node: is adding a message to the buffer from RECEIVETEST");
            this.bufferMessages.add(tM);
            return;
        } else {
            if(s_FN != this.node.getNameFragement()) {

                String url = edge.getUrl();

                try{
                    DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) Naming.lookup(url);
                    receiver.receiveAccept(this.node.getNodeNumber());
                } catch (RemoteException | NotBoundException | MalformedURLException e) {
                    System.out.println("For process: " + processID + " an error occured sending INITIATE, error: " + e);
                    e.printStackTrace();
                }
            } else {
                if(edge.getEdgeState() == UNKNOWN_MST) {
                    edge.setEdgeStateNOTMST();
                }

                if(this.node.getCurrentTestEdge() != null && this.node.getCurrentTestEdge().getLinkToNode() != s_NN ) {
                    //System.out.println("Current node: " + this.node.getNodeNumber() + " linked to node: " + this.node.getCurrentTestEdge().getLinkToNode() + " sender was: " + s_NN);

                    String url = edge.getUrl();

                    try{
                        DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) Naming.lookup(url);
                        receiver.receiveAccept(this.node.getNodeNumber());
                    } catch (RemoteException | NotBoundException | MalformedURLException e) {
                        System.out.println("For process: " + processID + " an error occured sending INITIATE, error: " + e);
                        e.printStackTrace();
                    }


                } else {

                    sendTest();
                }


            }
        }

        checkBuffer(TEST, PRINT_BUFFER);
    }

    @Override
    public void receiveAccept(int s_NN) {

        System.out.println(processID + " Node: has received ACCEPT message from node: " + s_NN);

        Edge edge = this.node.getEdgeWithConnection(s_NN);

        this.node.setCurrentTestEdge(null);

        if( this.node.getBestEdge() == null || edge.getWeight() < this.node.getBestEdge().getWeight() ) {
            this.node.setBestEdge(edge);
        }
        sendReport();
    }

    @Override
    public void receiveReject(int s_NN) {

        System.out.println(processID + " Node: has received REJECT message from node: " + s_NN);

        Edge edge = this.node.getEdgeWithConnection(s_NN);

        if(edge.isUnkownInMST()) {
            edge.setEdgeStateNOTMST();
        }
        sendTest();
    }

    @Override
    public void sendReport() {

        String url = this.node.getInBranch().getUrl();
        if( findCount == 0 && this.node.getCurrentTestEdge() == null ) {
            this.node.setNodeStateFOUND();

            if(this.node.getBestEdge() == null) {
                this.node.updateBestMOE();
            }

            ReportMessage rM = new ReportMessage(this.node.getNodeNumber(), this.node.getBestEdge().getWeight());

            try{
                DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) Naming.lookup(url);
                receiver.receiveReport(rM);
            } catch (RemoteException | NotBoundException | MalformedURLException e) {
                System.out.println("For process: " + processID + " an error occured sending INITIATE, error: " + e);
                e.printStackTrace();
            }
        }

    }

    @Override
    public void receiveReport(ReportMessage rM) {

        System.out.println(processID + " Node: has received an REPORT from node: " + rM.getNodeNumber());

        int s_NN = rM.getNodeNumber();
        int w = rM.getBestWeight();

        Edge edge = this.node.getEdgeWithConnection(s_NN);

        if( s_NN != this.node.getInBranch().getLinkToNode() ) {
            this.findCount = this.findCount - 1;
            if( w < this.node.getBestEdge().getWeight() ) {
                this.node.setBestEdge(edge);
            }
            sendReport();
        } else {

            if( this.node.getNodeState() == FIND) {
                this.bufferMessages.add(rM);
                return;
            } else {
                if( w > this.node.getBestEdge().getWeight()) {
                    sendChangeRoot();
                } else {
                    if( w == this.node.getBestEdge().getWeight()) {
                        System.out.println(processID + " Node: Has reached HALT");
                        //HALT
                    }
                }
            }

        }
        checkBuffer(REPORT, PRINT_BUFFER);
    }

    @Override
    public void sendChangeRoot(){

        System.out.println("I want ot change root");

        String url = this.node.getBestEdge().getUrl();

        if( this.node.getBestEdge().getEdgeState() == IN_MST) {

            try{
                DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) Naming.lookup(url);
                receiver.receiveChangeRoot(this.node.getNodeNumber());
            } catch (RemoteException | NotBoundException | MalformedURLException e) {
                System.out.println("For process: " + processID + " an error occured sending INITIATE, error: " + e);
                e.printStackTrace();
            }
        } else {

            sendConnect(url, this.node.getLevelFragement());
            this.node.getBestEdge().setEdgeStateINMST();

        }

    }

    @Override
    public void receiveChangeRoot(int s_NN) {

        System.out.println(processID + " Node: has received CHANGEROOT message from node: " + s_NN);

        sendChangeRoot();
    }

    @Override
    public void sendMessage(String url, String message) {

    }

    @Override
    public void receive(String message) {
        System.out.println();
    }

    public void checkBuffer(int type, boolean print) {

        if(print) {
            System.out.println(processID + " Node: BUFFER SIZE is currently: " +
                    bufferMessages.size() +
                    " and was checked in function: " +
                    messageTypeToString(type));
        }


        BufferMessage bM;

        for (int i = 0; i < this.bufferMessages.size(); i++) {
            bM = this.bufferMessages.get(i);
            if(bM.getMessageType() == CONNECT && type == CONNECT) {
                this.bufferMessages.remove(i);
                receiveConnect((ConnectMessage) bM);
            } else if(bM.getMessageType() == REPORT && type == REPORT) {
                this.bufferMessages.remove(i);
                receiveReport((ReportMessage) bM);
            } else if(bM.getMessageType() == TEST && type == TEST) {
                this.bufferMessages.remove(i);
                receiveTest((TestMessage) bM);
            }

        }


    }

    public void run() {

    }

    public void wakeUpNode() {
        if (this.node.getNodeState() == SLEEPING) {
            wakeUp();
        }
    }


    /**
     * Created Messages to help debug and see what is going on in alg
     */

    public void wakeUpMessage(Edge edge) {
        System.out.println("This node: " + node.getNodeNumber() +
                "\n best edge is linked to: " + edge.getLinkToNode() +
                "\n the weight is: " + edge.getWeight() +
                "\n the state of the edge is: " + edge.getEdgeStateString());
    }

    public String messageTypeToString(int messageType) {
        if(messageType == CONNECT) {
            return "CONNECT";
        } else if(messageType == TEST) {
            return "TEST";
        } else if(messageType == REPORT) {
            return "REPORT";
        } else {
            return "WEIRD INPUT GIVEN TO MESSAGE";
        }
    }
}
