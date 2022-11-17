package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;
import java.net.Socket;

public class ConnectionSuccessEvent extends NetworkEvent
{
    public Socket socket;
    public String ip;

    public ConnectionSuccessEvent(Socket socket)
    {
        super(NETWORK_EVENT_TYPE.NETWORK_EVENT_CONNECTION_SUCCESS);
        this.ip = socket.getInetAddress().toString().split("/")[1];
        this.socket = socket;
    }
}
