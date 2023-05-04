package remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteClient extends Remote {
    void sendMessageLocally(String msg);
    public void setManager(boolean isManager);

    void getNotificationAndClose(String s);

    boolean isManager();

    String getName();

    boolean askJoin(String name);

    void updateUserList(ArrayList<String> userList);
    void close();
}
