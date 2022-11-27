package ClavarChat.Models.Events;

public class ConnectionEvent extends Event
{
    public enum CONNECTION_STATUS { SUCCESS, FAILED, ENDED }

    public int socketID;
    public String dstIp;
    public String srcIp;
    public int dstPort;
    public int srcPort;
    public CONNECTION_STATUS status;

    public ConnectionEvent(CONNECTION_STATUS status, String dstIp, int dstPort, String srcIp, int srcPort, int socketID)
    {
        super(EVENT_TYPE.EVENT_NETWORK_CONNECTION);

        this.dstIp = dstIp;
        this.srcIp = srcIp;

        this.dstPort = dstPort;
        this.srcPort = srcPort;

        this.status = status;
        this.socketID = socketID;
    }

    public ConnectionEvent(CONNECTION_STATUS status, String dstIp, int dstPort, int socketID)
    {
        super(EVENT_TYPE.EVENT_NETWORK_CONNECTION);
        this.dstIp = dstIp;
        this.srcIp = null;

        this.dstPort = dstPort;
        this.srcPort = -1;

        this.status = status;
        this.socketID = socketID;
    }
}
