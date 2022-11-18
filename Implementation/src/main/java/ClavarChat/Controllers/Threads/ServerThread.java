package ClavarChat.Controllers.Threads;

import java.net.InetAddress;

public class ServerThread extends NetworkThread
{
    protected InetAddress ip;
    protected int port;

    protected ServerThread(int port)
    {
        this.port = port;
        this.ip = null;
    }
}
