package ClavarChat.Controllers.Threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPMessaginThread extends NetworkThread
{
    protected Socket socket;
    protected InetAddress ip;
    protected int port;

    protected TCPMessaginThread(String ip, int port) throws UnknownHostException
    {
        super();
        this.socket = new Socket();
        this.ip = InetAddress.getByName(ip);
        this.port = port;
    }

    protected TCPMessaginThread(Socket socket)
    {
        super();
        this.socket = socket;
        this.ip = socket.getInetAddress();
        this.port = socket.getPort();
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
