package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteBoard extends Remote {
    public String sayHello(IRemoteClient who) throws RemoteException;
}
