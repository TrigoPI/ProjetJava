package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.CONNECTION_EVENT_TYPE;

import java.net.Socket;

public class SuccessConectionEvent extends ConnectionEvent
{
    public Socket socket;
    public String ip;
    public int port;
    public boolean connected;

    public SuccessConectionEvent(Socket socket, String ip, int port)
    {
        super(CONNECTION_EVENT_TYPE.CONNECTION_EVENT_SUCCESS);
        this.ip = ip;
        this.port = port;
        this.socket = socket;
        this.connected = socket.isConnected();
    }
}
