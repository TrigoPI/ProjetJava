package ClavarChat.Models.Events;

import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;

public class SocketDataEvent extends Event
{
    public ClavarChatMessage data;

    public String srcIp;
    public String dstIp;
    public int srcPort;
    public int dstPort;

    public SocketDataEvent(String srcIp, int srcPort, String dstIp, int dstPort, ClavarChatMessage data)
    {
        super(EVENT_TYPE.EVENT_NETWORK_SOCKET_DATA);

        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.data = data;
    }
}
