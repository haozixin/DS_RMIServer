package remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteBoard extends Remote {
    boolean broadcastMessage(String username, String message) throws RemoteException;
    boolean createOrJoinBoard(IRemoteClient user) throws RemoteException;

    boolean closeAndNotifyAllUsers(String managerName)throws RemoteException;

    boolean existBoard(String userName) throws RemoteException;
    boolean isRepeated(String name) throws RemoteException;
}
