import remoteInterfaces.IRemoteServiceSkeleton;
import utils.PropertiesUtil;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Author:  Zixin Hao
 * Student ID: 1309180
 */
public class Server {
    private static final String S = "serviceName";
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Please input parameters first. Usage: Main <server-port>");
            System.exit(1);
        }

        // Parse and check whether the server port number is valid
        String serverPortStr = args[0];
        int serverPort;
        try {
            serverPort = Integer.parseInt(serverPortStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid server port, Usage: Main <server-port>");
            System.exit(1);
            return;
        }
        if (serverPort < 1024 || serverPort > 49151) {
            System.out.println("Server port out of range");
            System.exit(1);
        }

        try {
            IRemoteServiceSkeleton server = new RemoteServiceSkeletonObj(); // Create a remote object, 因为继承了UnicastRemoteObject
            Registry registry = LocateRegistry.createRegistry(serverPort); // Create the registry on the server
            registry.bind(PropertiesUtil.getConfig(S,PropertiesUtil.SERVER_CONFIG_PROPERTIES), server); // Bind the remote object's stub in the registry
            System.out.println("(\"Remote object bound to registry.\")  HellowWorld RMI Server is running...(listening on port " + serverPortStr + ")");
        } catch (Exception e) {
            System.out.println("Something wrong with binding services");
        }
    }
}
