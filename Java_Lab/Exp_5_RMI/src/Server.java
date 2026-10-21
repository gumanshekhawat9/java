import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

public class Server {
    public static void main(String[] args) {
        try {
            // Start the RMI registry inside this program (default port 1099).
            // If one is already running (for example started with rmiregistry),
            // the ExportException is ignored and that registry is used instead.
            try {
                LocateRegistry.createRegistry(1099);
            } catch (ExportException e) {
                System.out.println("Registry already running, using it.");
            }

            MyRemote obj = new MyRemote();
            Naming.rebind("rmi://localhost/Add", obj);

            System.out.println("Server is ready...");
        } catch (Exception e) {
            System.out.println("Server error: " + e);
        }
    }
}