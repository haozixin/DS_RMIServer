import remote.IRemoteBoard;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BoardServant extends UnicastRemoteObject implements IRemoteBoard {
    public BoardServant() throws RemoteException {
        super();
    }
    @Override
    public String sayHello(String who) throws RemoteException {
        System.out.println("Hello " + who + "!");
        return null;
    }
}


