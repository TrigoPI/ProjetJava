package ClavarChat.Controllers.Managers.Network;

import java.io.Serializable;

public class NetworkPacket implements Serializable
{
    public String srcIp;
    public String dstIp;

    public int srcPort;
    public int dstPort;

    public Serializable data;

    public NetworkPacket(String srcIp, int srcPort, String dstIp, int dstPort, Serializable data)
    {
        this.srcIp = srcIp;
        this.dstIp = dstIp;

        this.srcPort = srcPort;
        this.dstPort = dstPort;

        this.data = data;
    }
}
