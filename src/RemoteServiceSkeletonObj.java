import remoteInterfaces.IRemoteServiceSkeleton;
import remoteInterfaces.IRemoteServiceStub;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Author:  Zixin Hao
 * Student ID: 1309180
 */
public class RemoteServiceSkeletonObj extends UnicastRemoteObject implements IRemoteServiceSkeleton {
    private ArrayList<IRemoteServiceStub> clientList = new ArrayList<>();
    // messages are share resources
    private ArrayList<String> messages = new ArrayList<>();
    private String managerName;



    public RemoteServiceSkeletonObj() throws RemoteException {
        super();
    }

    @Override
    public synchronized boolean broadcastMessage(String username, String message) throws RemoteException {
        if (username == null || message == null) {
            return false;
        }
        String msg = username + ": " + message;
        messages.add(msg);
        for (IRemoteServiceStub client : clientList) {
            client.sendMessageLocally(msg);
        }
        return true;
    }

    @Override
    public synchronized boolean createOrJoinBoard(IRemoteServiceStub user) throws RemoteException {

        // create board; true is manager
        if (clientList.size() == 0) {
            user.setManager(true);
            clientList.add(user);
            updateUserList();
            managerName = user.getName();
            return true;
        }
        // join board; false is user (not manager)
        else {
            user.setManager(false);
            if (managerAgreeJoin(user.getName())) {
                clientList.add(user);
                updateUserList();
                return true;
            }
            return false;
        }
    }

    private void updateUserList() {
        ArrayList<String> userList = new ArrayList<>();
        for (IRemoteServiceStub client : clientList) {
            if (client.isManager()) {
                userList.add(client.getName() + "(Manager)");
                continue;
            }
            userList.add(client.getName());
        }
        for (IRemoteServiceStub client : clientList) {
            client.updateUserList(userList);
        }
    }


    private boolean managerAgreeJoin(String joinUserName) throws RemoteException {
        for (IRemoteServiceStub client : clientList) {
            if (client.isManager()) {
                return client.askJoin(joinUserName);
            }
        }
        return false;
    }

    /**
     * close board and notify all users
     *
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
        ArrayList<IRemoteServiceStub> temp = new ArrayList<>(clientList);
        for (IRemoteServiceStub client : temp) {
            clientList.remove(client);
            if (!client.isManager()) {
                System.out.println("remove client - " + client.getName());
                // notify the client
                client.getNotificationAndClose(managerName + "(Manager) has closed the board");
            } else {
                System.out.println("removed manager - " + client.getName());
                // manager is removed, doesn't need to be notified, since only manager can close the board(call the method)
            }
        }
        System.out.println("The size of client list is: " + clientList.size());
        return isSuccessful;
    }

    /**
     * remove the user from client list
     *
     * @param userName
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean existBoard(String userName) throws RemoteException {
        if (userName == null) {
            return false;
        }
        for (IRemoteServiceStub client : clientList) {
            if (client.getName().equals(userName)) {
                System.out.println("client - " + client.getName() + ", has leaved the board");
                clientList.remove(client);
                updateUserList();
                client.close();
                System.out.println("existBoard: The size of client list is: " + clientList.size());
                return true;
            }
        }
        System.out.println("existBoard: The size of client list is: " + clientList.size());
        return false;
    }

    @Override
    public boolean isRepeated(String name) {
        for (IRemoteServiceStub client : clientList) {
            if (client.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void kickOut(String name) throws RemoteException {
        for (IRemoteServiceStub client : clientList) {
            if (client.getName().equals(name)) {
                client.getNotificationAndClose("You are kicked out by manager");
                clientList.remove(client);
                updateUserList();
                System.out.println(name + " is kicked out; The size of client list is: " + clientList.size());
                return;
            }
        }
    }

    @Override
    public synchronized void synDraw(String name, String mode, Point start, Point end, Color color, String textDraw) throws RemoteException {
        for (IRemoteServiceStub client : clientList) {
            if (!client.getName().equals(name)) {
                client.draw(mode, start, end, color, textDraw);
            }
        }
    }

    @Override
    public void newCanvas() throws RemoteException {

        for (IRemoteServiceStub client : clientList) {
            if (!client.isManager()) {
                client.newCanvas();
            }
        }
    }

    @Override
    public boolean synImage(String name) throws RemoteException {
        boolean isSuccessful = false;
        byte[] imageByte = null;
        for (IRemoteServiceStub client : clientList) {
            if(client.getName().equals(managerName)){
                imageByte = client.sendImage(null);
            }
            if (client.getName().equals(name) && imageByte != null) {
                client.receiveImage(imageByte);
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    @Override
    public void loadImage(String name, String path) throws RemoteException {
        byte[] imageBytes;
        for (IRemoteServiceStub client : clientList) {
            if (client.getName().equals(name)) {
                imageBytes = client.sendImage(path);
                for (IRemoteServiceStub other : clientList) {
                    if (!other.getName().equals(name)) {
                        other.receiveImage(imageBytes);
                    }
                }
                break;
            }
        }


    }
}


