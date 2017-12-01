package tudelft.da;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by yuupv on 29-Nov-17.
 */
public class DA_Suzuki_Kasami_Main {

    public static void main(String[] args) {

        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            System.out.println("Error occured trying to create Registry 1099, err: " + e);
            e.printStackTrace();
        }

        new ProcessManager().startNetwork();

    }

}
