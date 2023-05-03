package remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteClient extends Remote {
    void sendMessageLocally(String msg);
    public void setManager(boolean isManager);

    void getNotificationAndClose(String s);

    boolean isManager();

    String getName();
}
