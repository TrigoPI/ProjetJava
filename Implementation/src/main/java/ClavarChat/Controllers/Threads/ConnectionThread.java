package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.SuccessConectionEvent;
import ClavarChat.Utils.Log.Log;

import java.io.IOException;
import java.net.*;

public class ConnectionThread extends NetworkThread
{
    private Socket socket;
    private String distantIP;
    private int distantPort;

    public ConnectionThread(String distantIP, int distantPort)
    {
        super();
        this.socket = new Socket();
        this.distantIP = distantIP;
        this.distantPort = distantPort;
    }

    @Override
    protected void update()
    {
        Log.Info(this.getClass().getName() + " RUN : " + this.distantIP + ":" + this.distantPort);
        this.connect();
        this.eventManager.notiy(new SuccessConectionEvent(socket, this.distantIP, this.distantPort));
        Log.Info(this.getClass().getName() + " : " + this.distantIP + ":" + this.distantPort + " finished");
    }

    private void connect()
    {
        try
        {
            InetAddress addr = InetAddress.getByName(this.distantIP);
            SocketAddress socketAddr = new InetSocketAddress(addr, this.distantPort);
            this.socket.connect(socketAddr, 10000);
        }
        catch (IOException e) { Log.Warning(this.getClass().getName() + " connection failed with : " + this.distantIP + ":" + this.distantPort); }
    }
}
