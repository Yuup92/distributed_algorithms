package da.tudelft.ghs;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class DA_Gallager_Humblet_Main {

    public static void main(String[] args) {

        System.out.println("Present Project Directory : "+ System.getProperty("user.dir"));

        new ProcessManager().startNetwork();


    }


}
