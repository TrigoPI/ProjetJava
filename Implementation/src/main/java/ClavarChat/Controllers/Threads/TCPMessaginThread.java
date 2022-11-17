package ClavarChat.Controllers.Threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPMessaginThread extends NetworkThread
{
    protected Socket socket;
    protected InetAddress distantIP;
    protected int distantPort;

    protected TCPMessaginThread(String ip, int port) throws UnknownHostException
    {
        super();
        this.socket = new Socket();
        this.distantIP = InetAddress.getByName(ip);
        this.distantPort = port;
    }

    protected TCPMessaginThread(Socket socket)
    {
        super(socket.getLocalAddress(), socket.getLocalPort());
        this.socket = socket;
        this.distantIP = socket.getInetAddress();
        this.distantPort = socket.getPort();
    }

    public int getDistantPort()
    {
        return this.distantPort;
    }

    public String getDistantIP()
    {
        return this.distantIP.toString().split("/")[1];
    }

    @Override
    public void close()
    {
        try
        {
            this.socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
