package da.tudelft.bufferMessage;

import da.tudelft.ghs.DA_Gallager_Humblet_Spira;

/**
 * Created by yuupv on 10-Jan-18.
 */
public class BufferChecker implements Runnable {

    private DA_Gallager_Humblet_Spira p;

    public BufferChecker(DA_Gallager_Humblet_Spira p) {
        this.p = p;
    }

    public void run() {
    try {
        while(true){
            Thread.sleep(1000);
            this.p.checkBuffer(0, false);
            this.p.checkBuffer(1, false);
            this.p.checkBuffer(2, false);
        }
    } catch (InterruptedException e) {
        e.printStackTrace();
    }


    }

}
