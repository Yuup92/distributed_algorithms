package da.tudelft;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Locale;

public class DA_Gallager_Humblet_Main {

    public static void main(String[] args) {

        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            System.out.println("Could not create a registry on port 1099, error "  + e);
            e.printStackTrace();
        }

        /**
         * System.setProperty() needs to be run before System.getSecurityManager()
         *   Otherwise an exception will be thrown for not having access to the java.policy file
         *
         *   TODO:
         *     - Make the location of java.policy easier to find
         *     - Make the location string of java.policy more generic
         */
        System.setProperty("java.security.policy", "D:/Trial/distributed_algorithms/Assignment3/src/main/resources/java.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        new ProcessManager().startNetwork();


    }


}
