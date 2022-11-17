package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;
import java.net.Socket;

public class NewConnectionEvent extends NetworkEvent
{
    public Socket socket;
    public String ip;
    public int port;

    public NewConnectionEvent(Socket socket)
    {
        super(NETWORK_EVENT_TYPE.NETWORK_EVENT_NEW_CONNECTION);
        this.ip = socket.getInetAddress().toString().split("/")[1];
        this.port = socket.getPort();
        this.socket = socket;
    }
}
