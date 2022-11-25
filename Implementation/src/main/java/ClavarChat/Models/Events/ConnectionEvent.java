package ClavarChat.Models.Events;

import java.net.Socket;

public class ConnectionEvent extends Event
{
    public enum CONNECTION_STATUS { SUCCESS, FAILED, ENDED }

    public Socket socket;
    public String distantIP;
    public int distantPort;
    public CONNECTION_STATUS status;

    public ConnectionEvent(CONNECTION_STATUS status, String distantIP, int distantPort, Socket socket)
    {
        super(EVENT_TYPE.EVENT_NETWORK_PAQUET);

        this.distantIP = distantIP;
        this.distantPort = distantPort;
        this.status = status;
        this.socket = socket;
    }
}
