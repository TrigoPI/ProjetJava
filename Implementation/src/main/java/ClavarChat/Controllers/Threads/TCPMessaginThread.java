package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.EndConnectionEvent;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;

import java.net.InetAddress;
import java.net.Socket;

public abstract class TCPMessaginThread extends NetworkThread
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

        this.running = false;

        this.distantIP = NetworkUtils.inetAddressToString(socket.getInetAddress());
        this.localIP = NetworkUtils.inetAddressToString(socket.getLocalAddress());
        this.distantPort = socket.getPort();
        this.localPort = socket.getLocalPort();
    }

    protected abstract void loop();

    public void stopSocket()
    {
        this.running = false;
    }

    public void startSocket()
    {
        this.running = true;
    }

    @Override
    protected void update()
    {
        this.startSocket();
        this.loop();
        this.stopSocket();
        this.eventManager.notiy(new EndConnectionEvent(this.distantIP));
    }
}
