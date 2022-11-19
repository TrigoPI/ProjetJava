package ClavarChat.Controllers.Threads;

import ClavarChat.Utils.NetworkUtils.NetworkUtils;

import java.net.InetAddress;
import java.net.Socket;

public class TCPMessaginThread extends NetworkThread
{
    protected Socket socket;
    protected String distantIP;
    protected String localIP;
    protected int distantPort;
    protected int localPort;
    protected boolean running;

    protected TCPMessaginThread(Socket socket)
    {
        super();
        this.socket = socket;
        this.distantIP = NetworkUtils.inetAddressToString(socket.getInetAddress());
        this.localIP = NetworkUtils.inetAddressToString(socket.getLocalAddress());
        this.distantPort = socket.getPort();
        this.localPort = socket.getLocalPort();

        this.running = false;
    }

    public void stopSocket()
    {
        this.running = false;
    }
}
