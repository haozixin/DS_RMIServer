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
            user.setManager(false);
            if (managerAgreeJoin(user.getName())) {
                clientList.add(user);
                return true;
            }
            return false;
        }
    }



    private boolean managerAgreeJoin(String joinUserName) throws RemoteException {
        for(IRemoteClient client:clientList){
            if(client.isManager()){
                return client.askJoin(joinUserName);
            }
        }
        return false;
    }

    /**
     * close board and notify all users
     * @param managerName
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean closeAndNotifyAllUsers(String managerName) throws RemoteException {
        boolean isSuccessful = true;
        if (managerName == null) {
            return false;
        }
        ArrayList<IRemoteClient> temp = new ArrayList<>(clientList);
        for (IRemoteClient client:temp) {
            clientList.remove(client);
            if (!client.isManager()) {
                System.out.println("remove client - "+client.getName());
                // notify the client
                client.getNotificationAndClose(managerName + "(Manager) has closed the board");
            }else{
                System.out.println("removed manager - "+client.getName());
                // manager is removed, doesn't need to be notified, since only manager can close the board(call the method)
            }
        }
        System.out.println("closeAndNotifyAllUsers: The size of client list is: "+clientList.size());
        return isSuccessful;
    }

    /**
     * remove the user from client list
     * @param userName
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean existBoard(String userName) throws RemoteException {
        if (userName == null) {
            return false;
        }
        for (IRemoteClient client:clientList) {
            if (client.getName().equals(userName)) {
                System.out.println("client - "+client.getName()+", has leaved the board");
                clientList.remove(client);
                return true;
            }
        }
        System.out.println("existBoard: The size of client list is: "+clientList.size());
        return false;
    }

    @Override
    public boolean isRepeated(String name) {
        for (IRemoteClient client:clientList) {
            if (client.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}


