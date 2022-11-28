package ClavarChat.Models.NetworkPaquet;

import java.io.Serializable;

public class NetworkPaquet implements Serializable
{
    public String srcIp;
    public String dstIp;

    public int srcPort;
    public int dstPort;

    public Serializable data;

    public NetworkPaquet(String srcIp, int srcPort, String dstIp, int dstPort, Serializable data)
    {
        this.srcIp = srcIp;
        this.dstIp = dstIp;

        this.srcPort = srcPort;
        this.dstPort = dstPort;

        this.data = data;
    }
}
