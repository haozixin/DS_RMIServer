import remote.IRemoteBoard;
import remote.IRemoteClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BoardServant extends UnicastRemoteObject implements IRemoteBoard {
    private Manager manager;



    public BoardServant() throws RemoteException {
        super();
    }
    @Override
    public String sayHello(IRemoteClient who) throws RemoteException {
        System.out.println("Hello " + who + "!");
        return who.test();
    }
}


