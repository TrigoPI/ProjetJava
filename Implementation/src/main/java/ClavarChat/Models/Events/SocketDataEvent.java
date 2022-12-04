package ClavarChat.Models.Events;

import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;

public class SocketDataEvent extends Event
{
    public static final String SOCKET_DATA = "SOCKET_DATA";

    public ClavarChatMessage data;

    public String srcIp;
    public String dstIp;
    public int srcPort;
    public int dstPort;

    public SocketDataEvent(String srcIp, int srcPort, String dstIp, int dstPort, ClavarChatMessage data)
    {
        super(SOCKET_DATA);

        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.data = data;
    }
}
