package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.ConnectionEvent;
import ClavarChat.Models.Events.ConnectionEvent.CONNECTION_STATUS;
import ClavarChat.Utils.Log.Log;

import java.io.IOException;
import java.net.*;

public class ConnectionThread extends NetworkThread
{
    private CONNECTION_STATUS status;
    private Socket socket;
    private String distantIP;
    private int distantPort;

    public ConnectionThread(String distantIP, int distantPort)
    {
        super();
        this.socket = new Socket();
        this.distantIP = distantIP;
        this.distantPort = distantPort;
        this.status = CONNECTION_STATUS.SUCCESS;
    }

    @Override
    protected void update()
    {
        Log.Info(this.getClass().getName() + " RUN : " + this.distantIP + ":" + this.distantPort);
        this.connect();
        this.eventManager.notiy(new ConnectionEvent(this.status, this.distantIP, this.distantPort, this.socket));
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
        catch (IOException e)
        {
            Log.Warning(this.getClass().getName() + " connection failed with : " + this.distantIP + ":" + this.distantPort);
            this.status = CONNECTION_STATUS.FAILED;
        }
    }
}
