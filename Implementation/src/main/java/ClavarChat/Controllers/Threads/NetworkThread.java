package ClavarChat.Controllers.Threads;

import ClavarChat.Controllers.Managers.EventManager;

import java.net.InetAddress;

public abstract class NetworkThread extends Thread
{
    protected EventManager eventManager;

    protected InetAddress localIP;
    protected int localPort;

    protected NetworkThread()
    {
        this.eventManager = EventManager.getInstance();
        this.localIP = null;
        this.localPort = -1;
    }

    protected NetworkThread(InetAddress localIP, int localPort)
    {
        super();

        this.eventManager = EventManager.getInstance();
        this.localIP = localIP;
        this.localPort = localPort;
    }

    public String getLocalIP()
    {
        return this.localIP.toString().split("/")[1];
    }

    public int getLocalPort()
    {
        return this.localPort;
    }

    public String getIdString()
    {
        return "" + this.getId();
    }

    abstract public void close();
}
