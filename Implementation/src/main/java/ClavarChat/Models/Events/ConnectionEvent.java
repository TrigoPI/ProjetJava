package ClavarChat.Models.Events;

public class ConnectionEvent extends Event
{
    public enum CONNECTION_STATUS { SUCCESS, FAILED, ENDED }

    public int socketID;
    public String distantIP;
    public int distantPort;
    public CONNECTION_STATUS status;

    public ConnectionEvent(CONNECTION_STATUS status, String distantIP, int distantPort, int socketID)
    {
        super(EVENT_TYPE.EVENT_NETWORK_CONNECTION);

        this.distantIP = distantIP;
        this.distantPort = distantPort;
        this.status = status;
        this.socketID = socketID;
    }
}
