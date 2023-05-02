import remoteInterfaces.IRemoteBoard;
import remoteInterfaces.IRemoteClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RemoteBoardObj extends UnicastRemoteObject implements IRemoteBoard {
    private ArrayList<IRemoteClient> clientList = new ArrayList<>();
    // messages are share resources
    private ArrayList<String> messages = new ArrayList<>();



    public RemoteBoardObj() throws RemoteException {
        super();
    }

    @Override
    public synchronized boolean broadcastMessage(String username, String message) throws RemoteException {
        if (username == null || message == null) {
            return false;
        }
        String msg = username + ": " + message;
        messages.add(msg);
        for(IRemoteClient client:clientList){
            client.sendMessageLocally(msg);
        }
        return true;
    }

    @Override
    public synchronized boolean createOrJoinBoard(IRemoteClient user) throws RemoteException {
        // create board; true is manager
        if (clientList.size()==0) {
            user.setManager(true);
            clientList.add(user);
            return true;
        }
        // join board; false is user (not manager)
        else {
            clientList.add(user);
            return false;
        }
    }
}


