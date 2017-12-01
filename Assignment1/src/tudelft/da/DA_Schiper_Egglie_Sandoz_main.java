package tudelft.da;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

/**
 * Created by yuupv on 23-Nov-17.
 */
public class DA_Schiper_Egglie_Sandoz_main {

    public static void main(String[] args) {

        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }



        new ProcessManager().startNetwork();
    }
}
