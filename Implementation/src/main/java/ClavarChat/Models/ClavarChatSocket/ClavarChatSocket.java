package ClavarChat.Models.ClavarChatSocket;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Utils.Log.Log;

import java.io.Serializable;

public class ClavarChatSocket
{
    private int srcPort;
    private String srcIp;
    private final int socketId;
    private final int dstPort;
    private final String dstIp;

    private final NetworkManager networkManager;

    public ClavarChatSocket(int socketId, String srcIp, int srcPort, String dstIp, int dstPort, NetworkManager networkManager)
    {
        this.networkManager = networkManager;
        this.socketId = socketId;
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
    }

    public ClavarChatSocket(int socketId, String dstIp, int dstPort, NetworkManager networkManager)
    {
        this.networkManager = networkManager;
        this.socketId = socketId;
        this.srcIp = null;
        this.srcPort = -1;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
    }

    public int getSocketId()
    {
        return socketId;
    }

    public int getSrcPort()
    {
        return srcPort;
    }

    public int getDstPort()
    {
        return dstPort;
    }

    public String getSrcIp()
    {
        return srcIp;
    }

    public String getDstIp()
    {
        return dstIp;
    }

    public int connect()
    {
        int code = this.networkManager.connect(this.socketId, this.dstIp, this.dstPort);

        if (code < 0)
        {
            Log.Warning(this.getClass().getName() + " Connection failed with : " + this.dstIp + ":" + this.dstPort);
            return code;
        }

        this.srcIp   = this.networkManager.getLocalSocketIp(this.socketId);
        this.srcPort = this.networkManager.getLocalSocketPort(this.socketId);

        return code;
    }

    public Serializable receive()
    {
        Serializable data = this.networkManager.tcpReceive(this.socketId);
        Log.Info(this.getClass().getName() + " Receiving data : " + this.srcIp + ":" + this.srcPort + " <-- " + this.dstIp + ":" + this.dstPort);
        return data;
    }

    public void send(Serializable data)
    {
        Log.Info(this.getClass().getName() + " Sending data : " + this.srcIp + ":" + this.srcPort + " --> " + this.dstIp + ":" + this.dstPort);
        this.networkManager.tcpSend(this.socketId, data);
    }
}
