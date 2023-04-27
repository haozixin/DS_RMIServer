import remote.IRemoteBoard;
import utils.PropertiesUtil;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class Server {
    private static final String S = "serviceName";
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Please input parameters first. Usage: Main <server-address> <server-port>");
            System.exit(1);
        }
        // Resolve and check whether the server address is valid
        String serverAddress = args[0];
        try {
            InetAddress.getByName(serverAddress);
        } catch (UnknownHostException e) {
            System.out.println("Invalid server address, Usage: Main <server-address> <server-port>");
            System.exit(1);
        }

        // Parse and check whether the server port number is valid
        String serverPortStr = args[1];
        int serverPort;
        try {
            serverPort = Integer.parseInt(serverPortStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid server port, Usage: Main <server-address> <server-port>");
            System.exit(1);
            return;
        }
        if (serverPort < 1024 || serverPort > 49151) {
            System.out.println("Server port out of range");
            System.exit(1);
        }

        try {
            LocateRegistry.createRegistry(serverPort); // Create the registry on the server
            IRemoteBoard server = new BoardServant(); // Create a remote object, 因为继承了UnicastRemoteObject
            Naming.rebind("rmi://"
                    + serverAddress
                    +":"+ serverPortStr + "/"
                    + PropertiesUtil.getConfig(S,PropertiesUtil.SERVER_CONFIG_PROPERTIES), server); // Bind the remote object's stub in the registry
            System.out.println("(\"Remote object bound to registry.\")  HellowWorld RMI Server is running...(listening on port " + serverPortStr + ")");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
