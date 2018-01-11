package da.tudelft.Network;

import da.tudelft.datastructures.Edge;
import da.tudelft.datastructures.Node;

import java.util.ArrayList;
import java.util.Random;

public class FullNetwork {

    public FullNetwork(){}

    public static ArrayList<Node> createFullNetwork(int procAmount, String[] urls) {

        Random rnd = new Random();
        ArrayList<Node> nL = new ArrayList<>();
        int edgeAmount = procAmount*(procAmount - 1)/2;
        ArrayList<Integer> weightList = new CircleNetwork().createWeightList((edgeAmount+1));
        Node node;
        Edge edge;
        int weight;

        ArrayList<Integer> completed = new ArrayList<>();

        for (int i = 0; i < procAmount; i++) {

            node = new Node(i, urls[i]);


            for (int j = 0; j < procAmount; j++) {

                if(j != i && !completed.contains(j)) {
                    //This generates random weights for the network if true otherwise the
                    // weights for the network will go from [1,(Node Amount + 1)]
                    if(true) {
                        int v = rnd.nextInt(weightList.size());
                        weight = weightList.get(v);
                        weightList.remove(v);
                    } else {
                        weight = weightList.get(0);
                        weightList.remove(0);
                    }
                    edge = new Edge(j, weight, urls[j] );
                    node.addLink(edge);
                }

            }
            completed.add(i);

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

}
