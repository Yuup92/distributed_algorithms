package tudelft.da;

import java.rmi.RemoteException;

/**
 * Created by yuupv on 29-Nov-17.
 */
public class Delay implements Runnable {

    private DA_Schiper_Eggli_Sandoz p;
    private Message m;

    public Delay(DA_Schiper_Eggli_Sandoz p, Message m) {
        this.p = p;
        this.m = m;
    }

    public void run() {
        try{
            Thread.sleep(1000);
            this.m.setDelay(false);
            this.p.receive(this.m);
            Thread.currentThread().interrupt();
        } catch (InterruptedException | RemoteException e) {
            System.err.println("Something went wrong during the sleeping of the thread");
            e.printStackTrace();
        }
    }
}
