import remote.IRemoteBoard;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class Server {
    public static void main(String[] args) {
        String hostName = "localhost";
        String serviceName = "HelloWorldService";
        int port = 8000;

        if (args.length == 2) {
            hostName = args[0];
            serviceName = args[1];
        }
        try {
            LocateRegistry.createRegistry(port); // Create the registry on the server
            IRemoteBoard server = new BoardServant(); // Create a remote object, 因为继承了UnicastRemoteObject
            Naming.rebind("rmi://" + hostName +":"+ port + "/" + serviceName, server); // Bind the remote object's stub in the registry
            System.out.println("(\"Remote object bound to registry.\");  HellowWorld RMI Server is running...");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
