package da.tudelft.ghs;

import da.tudelft.bufferMessage.*;
import da.tudelft.datastructures.Edge;
import da.tudelft.datastructures.Node;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

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


    //Debugging variable
    private final static boolean PRINT_BUFFER = false;
    private final static boolean DELAY_PROCESSES = true;
    private final static boolean ONLY_SHOW_INBRANCH = false;
    private final static boolean SHOW_SEND_MESSAGES = true;
    private final static boolean BUFFER_CHECK_THREAD = true;

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

        randomDelay(DELAY_PROCESSES);

        //printStatus();

        //System.out.println(this.processID + " Node: has received WAKEUP");

        Edge edge = this.node.getBestEdge();
        edge.setEdgeStateINMST();

        this.node.setLevelFragment(0);
        this.node.setNodeStateFOUND();
        this.node.setFindCount(0);

        if(BUFFER_CHECK_THREAD) {
            new Thread(new BufferChecker(this)).start();
        }

        //This function system.out's
        //wakeUpMessage(edge);

        sendConnect(edge.getUrl(), 0);

    }

    @Override
    public synchronized void sendConnect(String url, int levelFragment) {

        if(SHOW_SEND_MESSAGES) {
            System.out.println(processID + " Node: is sending CONNECT to node: " + url);
            System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());

            //printStatus();
        }

        ConnectMessage cM = new ConnectMessage(this.node.getNodeNumber(), levelFragment, this.node);

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
    public  void receiveConnect(ConnectMessage cM)  {

        randomDelay(DELAY_PROCESSES);

        if(!ONLY_SHOW_INBRANCH){
            System.out.println(processID + " Node: is receiving CONNECT message from node: " + cM.getNode().getNodeNumber());
            System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());

            //printStatus();
        }

        Node senderNode = cM.getNode();
        int levelFragmentSender = cM.getNodeLevel();
        Edge edge = this.node.getEdgeWithConnection(senderNode.getNodeNumber());

        wakeUpNode();

        if (levelFragmentSender < this.node.getLevelFragement()) {

            System.out.println("I am here....");

            edge.setEdgeStateINMST();

//            if(this.node.getNameFragement() < 0) {
//                this.node.findNameFragment(senderNode);
//            }

            sendInitiate(edge.getUrl(), this.node.getLevelFragement(), this.node.getNameFragement(), this.node.getNodeState(), this.node.getNodeNumber());

            if (this.node.getNodeState() == FIND) {
                this.findCount++;
            }
        } else {
            if (edge.getEdgeState() == UNKNOWN_MST) {
                this.bufferMessages.add(cM);
                return;
            } else {
                int lvl = this.node.getLevelFragement() + 1;
                this.node.setLevelFragment( lvl  );
                System.out.println(processID + " Node: is going up to LEVEL " + lvl);
                System.out.println("It belongs to fragment " + node.getNameFragement());
                sendInitiate(edge.getUrl(), lvl, edge.getWeight(), FIND, this.node.getNodeNumber());
            }
        }

        checkBuffer(CONNECT, PRINT_BUFFER);

    }

    @Override
    public void sendInitiate(String url, int nodeLevel, int fragmentName, int nodeState, int nodeNumber) {

        if(SHOW_SEND_MESSAGES) {
            System.out.println(processID + " Node: is sending INITIATE to node: " + url);
            System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());

            //printStatus();
        }

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

        randomDelay(DELAY_PROCESSES);

        if(!ONLY_SHOW_INBRANCH) {
            System.out.println(processID + " Node: has received a INITIATE message from node: " + s_nodeNumber);
            System.out.println("It belongs to fragment" + node.getNameFragement());

            //printStatus();
        }

        this.node.setLevelFragment(s_nodeLevel);
        this.node.setNameFragment(s_fragmentName);
        this.node.setNodeState(s_nodeState);
        this.node.setInBranch(s_nodeNumber);

        this.node.resetBestEdge();
        this.node.setBestWeight(Integer.MAX_VALUE);

        ArrayList<Edge> listEdges = this.node.getNodeEdges();

        for (int i = 0; i < listEdges.size() ; i++) {
            Edge edge = listEdges.get(i);
            if(edge.getLinkToNode() != s_nodeNumber && edge.getEdgeState() == IN_MST) {
                sendInitiate(edge.getUrl(), s_nodeLevel, s_fragmentName, s_nodeState, this.node.getNodeNumber());
            }
            if(s_nodeState == FIND) {
                this.findCount++;
            }
        }
        if(s_nodeState == FIND) {
            //System.out.println(processID + " Node: has INBRANCH. URL: " + this.node.getInBranch().getUrl());
            sendTest();
        }


    }

    @Override
    public void sendTest() {

        if( this.node.checkEdgesNotInMST()) {

            String url = this.node.getCurrentTestEdge().getUrl();

            if(SHOW_SEND_MESSAGES) {
                System.out.println(processID + " Node: is sending TEST to node: " + url);
                System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());

                //printStatus();
            }

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

        randomDelay(DELAY_PROCESSES);

        if(!ONLY_SHOW_INBRANCH) {
            System.out.println(processID + " Node: has received TEST message from node: " + tM.getNodeNumber());
            System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());

            //printStatus();
        }

        int s_NN = tM.getNodeNumber();
        int s_LN = tM.getNodeLevel();
        int s_FN = tM.getFragementName();

        wakeUpNode();

        Edge edge = this.node.getEdgeWithConnection(s_NN);

        if( s_LN > this.node.getLevelFragement() ) {
            if(!ONLY_SHOW_INBRANCH) {
                System.out.println(processID + " Node: is adding a message to the buffer from RECEIVETEST");
            }
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
//if( this.node.getCurrentTestEdge().getLinkToNode() != s_NN ) {
    // System.out.println("Current node: " + this.node.getNodeNumber() + " linked to node: " + this.node.getCurrentTestEdge().getLinkToNode() + " sender was: " + s_NN);

                    String url = edge.getUrl();

                    try{
                        DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) Naming.lookup(url);
                        receiver.receiveReject(this.node.getNodeNumber());
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

        randomDelay(DELAY_PROCESSES);

        if(!ONLY_SHOW_INBRANCH) {
            System.out.println(processID + " Node: has received ACCEPT message from node: " + s_NN);
            System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());

            //printStatus();
        }

        Edge edge = this.node.getEdgeWithConnection(s_NN);

        this.node.setCurrentTestEdge(null);

        int weight;

        if(this.node.getBestEdge() == null) {
            weight = Integer.MAX_VALUE;
        } else {
            weight = this.node.getBestEdge().getWeight();
        }

        if( edge.getWeight() < this.node.getBestWeight() ) {
            this.node.setBestEdge(edge);
            this.node.setBestWeight(edge.getWeight());
        }
        sendReport();
    }

    @Override
    public void receiveReject(int s_NN) {

        randomDelay(DELAY_PROCESSES);

        if(!ONLY_SHOW_INBRANCH) {
            System.out.println(processID + " Node: has received REJECT message from node: " + s_NN);
            System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());

        }

        Edge edge = this.node.getEdgeWithConnection(s_NN);

        if(edge.isUnkownInMST()) {
            edge.setEdgeStateNOTMST();
        }
        sendTest();
    }

    @Override
    public void sendReport() {


        String url = "";
        try {
            url = this.node.getUrlEdgeInMST();
        } catch (NullPointerException e) {
            System.out.println(processID + " Node: INBRANCH is NULL");
            return;
        }
        if( this.findCount == 0 && this.node.getCurrentTestEdge() == null ) {
            this.node.setNodeStateFOUND();


            if(SHOW_SEND_MESSAGES) {
                System.out.println(processID + " Node: is sending REPORT to node: " + this.node.getUrlEdgeInMST());
                System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());

                //printStatus();
            }


//            if(this.node.getBestEdge() == null) {
//                this.node.updateBestMOE();
//            }

            ReportMessage rM = new ReportMessage(this.node.getNodeNumber(), this.node.getBestWeight());

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

        randomDelay(DELAY_PROCESSES);

        if(!ONLY_SHOW_INBRANCH) {
            System.out.println(processID + " Node: has received an REPORT from node: " + rM.getNodeNumber());
            System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());

            //printStatus();
        }
        int s_NN = rM.getNodeNumber();
        int w = rM.getBestWeight();

        Edge edge = this.node.getEdgeWithConnection(s_NN);

        if(s_NN != this.node.getInBranch() ) {
            this.findCount = this.findCount - 1;
            if( w < this.node.getBestWeight()) {
                this.node.setBestEdge(edge);
                this.node.setBestWeight(w);
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

        System.out.println("I want to change root");
        System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());


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

        this.node.setInBranch(s_NN);

        randomDelay(DELAY_PROCESSES);

        System.out.println(processID + " Node: has received CHANGEROOT message from node: " + s_NN);
        System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());


        sendChangeRoot();
    }

    @Override
    public void sendMessage(String url, String message) {

        try{
            Registry reg = LocateRegistry.getRegistry();
            DA_Gallager_Humblet_Spira_RMI receiver = (DA_Gallager_Humblet_Spira_RMI) reg.lookup("process2");
            receiver.receive(message);
        } catch (RemoteException | NotBoundException e) {
            System.out.println("For process: " + processID + " an error occured sending sendMessage, error: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void receive(String message) {
        int n = processID + 1;
        System.out.println(n + " has received a message: " + message);
        System.out.println("It belongs to fragment " + node.getNameFragement() + " and has level " + node.getLevelFragement());

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


    public void printStatus() {

        if(true){
            System.out.println(processID + " Node: Wants to tell its status \n    FragLevel: " + this.node.getLevelFragement() +
                    " \n   Fragment Name: " + this.node.getNameFragement() +
                    " \n   State of Node: " + this.node.getNodeState() +
                    " \n   Find-Count: " + this.findCount);
        } else {
            try {
                System.out.println(processID + " Node: Wants to tell its status \n   FragLevel: " + this.node.getLevelFragement() +
                        " \n   Fragment Name: " + this.node.getNameFragement() +
                        " \n State of Node: " + this.node.getNodeState() +
                        " \n In-Branch: " + this.node.getInBranch() +
                        " \n Test-Edge: " + this.node.getCurrentTestEdge().toString() +
                        " \n Best-Edge: " + this.node.getBestEdge() +
                        " \n Find-Count: " + this.findCount);
            } catch (NullPointerException e) {
                //e.printStackTrace();
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

    public void randomDelay(boolean delay) {

        if(delay) {
            Random rn = new Random();
            int n = rn.nextInt(400) + 200;
            try{
                Thread.sleep(n);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
