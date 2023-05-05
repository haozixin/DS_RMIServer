package remoteInterfaces;

import java.awt.*;
import java.rmi.Remote;
import java.util.ArrayList;

public interface IRemoteServiceStub extends Remote {
    void sendMessageLocally(String msg);
    void setManager(boolean isManager);

    void getNotificationAndClose(String s);

    boolean isManager();

    String getName();

    boolean askJoin(String name);

    void updateUserList(ArrayList<String> userList);
    void close();

    void draw(String mode, Point start, Point end, Color color, String textDraw);

    void newCanvas();
}
