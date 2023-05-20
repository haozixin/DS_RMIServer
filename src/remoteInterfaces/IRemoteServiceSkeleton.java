package remoteInterfaces;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Author:  Zixin Hao
 * Student ID: 1309180
 */
public interface IRemoteServiceSkeleton extends Remote {
    boolean broadcastMessage(String username, String message) throws RemoteException;
    boolean createOrJoinBoard(IRemoteServiceStub user) throws RemoteException;

    boolean closeAndNotifyAllUsers(String managerName)throws RemoteException;

    boolean existBoard(String userName) throws RemoteException;
    boolean isRepeated(String name) throws RemoteException;

    void kickOut(String name)throws RemoteException;
    void synDraw(String name, String mode, Point start, Point end, Color color, String textDraw)throws RemoteException;
    void newCanvas()throws RemoteException;
    boolean synImage(String name)throws RemoteException;
}
