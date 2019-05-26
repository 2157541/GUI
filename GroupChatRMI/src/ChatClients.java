
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class ChatClients extends UnicastRemoteObject implements ChatClientInt {

    private String name;
    private GUI ui;

    public ChatClients(String n) throws RemoteException {
        name = n;
    }

    @Override
    public void tell(String st) throws RemoteException {
        System.out.println(st);
        ui.writeMsg(st);
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    public void setGUI(GUI gui) {
        ui = gui;
    }
}
