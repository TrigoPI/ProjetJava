package ClavarChat.Controllers.Threads;

import java.net.InetAddress;
import java.net.Socket;

public class TCPMessaginThread extends NetworkThread
{
    protected Socket socket;
    protected InetAddress distantIP;
    protected InetAddress localIP;
    protected int distantPort;
    protected int localPort;
    protected boolean running;

    protected TCPMessaginThread(Socket socket)
    {
        super();
        this.socket = socket;
        this.distantIP = socket.getInetAddress();
        this.localIP = socket.getLocalAddress();
        this.distantPort = socket.getPort();
        this.localPort = socket.getLocalPort();

        this.running = false;
    }

    public void stopSocket()
    {
        this.running = false;
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
