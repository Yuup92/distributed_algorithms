package tudelft.da;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

import static java.lang.System.exit;

/**
 * Created by yuupv on 28-Nov-17.
 */
public class Buffer_Element implements Serializable {

    private int processNumber;
    private ArrayList<Integer> vectorClock;

    public Buffer_Element(int processNumber, ArrayList<Integer> initialVector) {
        this.processNumber = processNumber;
        this.vectorClock = initialVector;
    }

    public void updateVC(ArrayList<Integer> newVector){
        this.vectorClock = newVector;
    }

    public void updateElementVC(int processNumber, int newClockValue) {
        this.vectorClock.set(processNumber, newClockValue);
    }

    public int getVectorElement(int num) {
        return this.vectorClock.get(num);
    }

    public int getProcessNumber() {
        return this.processNumber;
    }

    public ArrayList<Integer> getVectorClock() {
        return this.vectorClock;
    }

    public String toString(){
        String l = vectorClock.toString();
        return l;
    }

    public boolean compareTimeStamps(ArrayList<Integer> ts){

        boolean b = true;

        if(ts.size() == this.vectorClock.size()) {
            for (int i = 0; i <ts.size() ; i++) {
                if(ts.get(i) < this.vectorClock.get(i)) {
                    b = false;
                }
            }
        } else {
            System.out.println("Size of buffer and time stamp are not the same....");
            exit(1);
        }
        return b;
    }

    /**
     * This function returns true if
     * @param current_S
     * @return
     */
    public boolean compareVectorClock(ArrayList<Integer> current_S) {
        boolean b = false;

        for (int i = 0; i <current_S.size() ; i++) {
            if(current_S.get(i) >= this.vectorClock.get(i)) {
                b = true;
            }
        }

        return b;
    }
}
