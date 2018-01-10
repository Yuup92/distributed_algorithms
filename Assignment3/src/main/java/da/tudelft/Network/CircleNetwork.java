package da.tudelft.Network;

import da.tudelft.datastructures.Edge;
import da.tudelft.datastructures.Node;

import java.util.ArrayList;
import java.util.Random;

public class CircleNetwork {

    private static Random rnd = new Random();

    public CircleNetwork(){}

    public static ArrayList<Node> createCircularNetwork(int procAmount, String[] urls) {

        ArrayList<Integer> weightList = createWeightList(procAmount);

        ArrayList<Node> nL = new ArrayList<>();
        Node node;
        Edge edge;
        int weight;

        for (int i = 0; i < procAmount; i++) {

            //This generates random weights for the network if true otherwise the
            // weights for the network will go from [1,(Node Amount + 1)]
            if(false) {
                int v = rnd.nextInt(weightList.size());
                weight = weightList.get(v);
                weightList.remove(v);
            } else {
                weight = weightList.get(0);
                weightList.remove(0);
            }


            node = new Node(i, urls[i]);

            if(i == (procAmount - 1)) {
                edge = new Edge(0, weight, urls[0]);
                node.addLink(edge);
            } else {
                edge = new Edge(i+1, weight, urls[i+1]);
                node.addLink(edge);
            }
            nL.add(node);

        }


        for (int i = 0; i < procAmount; i++) {
            for (int j = 0; j < procAmount; j++) {
                nL.get(i).checkConnection(nL.get(j));
            }
        }

        if(true) {
            for(int i = 0; i < procAmount; i++) {
                nL.get(i).showConnection();
            }
        }

        return nL;

    }

    public static ArrayList<Integer> createWeightList(int procAmount) {
        ArrayList<Integer> wL = new ArrayList<>();

        for (int i = 0; i < procAmount; i++) {
            wL.add( (i+1) );
        }

        return wL;
    }
}
