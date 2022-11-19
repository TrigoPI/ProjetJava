package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.CONNECTION_EVENT_TYPE;

import java.net.Socket;

public class SuccessConectionEvent extends ConnectionEvent
{
    public Socket socket;
    public String ip;
    public int port;

    public SuccessConectionEvent(Socket socket)
    {
        super(CONNECTION_EVENT_TYPE.CONNECTION_EVENT_SUCCESS);
        this.ip = socket.getInetAddress().toString().split("/")[1];
        this.port = socket.getPort();
        this.socket = socket;
    }
}
