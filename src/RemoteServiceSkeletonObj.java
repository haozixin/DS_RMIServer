import remoteInterfaces.IRemoteServiceSkeleton;
import remoteInterfaces.IRemoteServiceStub;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Dictionary;

public class RemoteServiceSkeletonObj extends UnicastRemoteObject implements IRemoteServiceSkeleton {
    private ArrayList<IRemoteServiceStub> clientList = new ArrayList<>();
    // messages are share resources
    private ArrayList<String> messages = new ArrayList<>();
    private ArrayList<ArrayList<String>> drawOptionsList = new ArrayList<>();


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
    public void synDraw(String name, String mode, Point start, Point end, Color color, String textDraw) throws RemoteException {
        ArrayList<String> drawOptions = new ArrayList<>();
        drawOptions.add(mode);
        drawOptions.add(String.valueOf(start.x));
        drawOptions.add(String.valueOf(start.y));
        drawOptions.add(String.valueOf(end.x));
        drawOptions.add(String.valueOf(end.y));
        drawOptions.add(String.valueOf(color.getRGB()));
        drawOptions.add(textDraw);
        drawOptionsList.add(drawOptions);
        for (IRemoteServiceStub client : clientList) {
            if (!client.getName().equals(name)) {
                client.draw(mode, start, end, color, textDraw);
            }
        }
    }

    @Override
    public void newCanvas() throws RemoteException {
        drawOptionsList.clear();
        for (IRemoteServiceStub client : clientList) {
            client.newCanvas();
        }
    }

    @Override
    public boolean synImage(String name) throws RemoteException {
        boolean isSuccessful = false;
        for (IRemoteServiceStub client : clientList) {
            if (client.getName().equals(name)) {
                for (ArrayList<String> drawOptions : drawOptionsList) {
                    String mode = drawOptions.get(0);
                    Point start = new Point(Integer.parseInt(drawOptions.get(1)), Integer.parseInt(drawOptions.get(2)));
                    Point end = new Point(Integer.parseInt(drawOptions.get(3)), Integer.parseInt(drawOptions.get(4)));
                    Color color = new Color(Integer.parseInt(drawOptions.get(5)));
                    String textDraw = drawOptions.get(6);
                    client.draw(mode, start, end, color, textDraw);
                }
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }
}


