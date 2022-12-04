package ClavarChat.Models.Events;

public class ConnectionEvent extends Event
{
    public static final String CONNECTION_SUCCESS = "CONNECTION_SUCCESS";
    public static final String CONNECTION_FAILED  = "CONNECTION_FAILED";
    public static final String CONNECTION_ENDED   = "CONNECTION_ENDED";

    public int socketID;
    public String dstIp;
    public String srcIp;
    public int dstPort;
    public int srcPort;

    public ConnectionEvent(String event, String dstIp, int dstPort, String srcIp, int srcPort, int socketID)
    {
        super(event);

        this.dstIp = dstIp;
        this.srcIp = srcIp;

        this.dstPort = dstPort;
        this.srcPort = srcPort;

        this.socketID = socketID;
    }

    public ConnectionEvent(String event, String dstIp, int dstPort, int socketID)
    {
        super(event);
        this.dstIp = dstIp;
        this.srcIp = null;

        this.dstPort = dstPort;
        this.srcPort = -1;

        this.socketID = socketID;
    }
}
