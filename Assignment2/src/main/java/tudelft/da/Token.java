package tudelft.da;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yuupv on 30-Nov-17.
 */
public class Token implements Serializable {

    private ArrayList<Integer> TN;
    public static boolean singleton = false;

    private Token(int numOfProcInNetwork) {
        TN = initializeTN(numOfProcInNetwork);
    }

    public static Token getToken(int numOfProcInNetwork) {
        if(!singleton) {
            singleton = true;
            Token t = new Token(numOfProcInNetwork);
            return t;
        }
        return null;
    }

    public ArrayList<Integer> getTN(){
        return this.TN;
    }

    public ArrayList<Integer> initializeTN(int num) {
        ArrayList<Integer> TN = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            TN.add(0);
        }

        return TN;
    }

}
