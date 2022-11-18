package ClavarChat.Controllers.Threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPMessaginThread extends NetworkThread
{
    protected Socket socket;
    protected InetAddress distantIP;
    protected InetAddress localIP;
    protected int distantPort;
    protected int localPort;

    protected TCPMessaginThread(Socket socket)
    {
        super();
        this.socket = socket;
        this.distantIP = socket.getInetAddress();
        this.localIP = socket.getLocalAddress();
        this.distantPort = socket.getPort();
        this.localPort = socket.getLocalPort();
    }

    public int getDistantPort()
    {
        return this.distantPort;
    }

    public int getLocalPort()
    {
        return this.localPort;
    }

    public String getDistantIP()
    {
        return this.distantIP.toString().split("/")[1];
    }

    public String getLocalIP()
    {
        return localIP.toString().split("/")[1];
    }
}
