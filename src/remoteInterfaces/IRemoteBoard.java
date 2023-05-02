package remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteBoard extends Remote {
    boolean broadcastMessage(String username, String message) throws RemoteException;
    boolean createOrJoinBoard(IRemoteClient user) throws RemoteException;
}
