package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.Enums.THREAD_EVENT_TYPE;
import ClavarChat.Models.Events.NewConnectionEvent;
import ClavarChat.Models.Events.ThreadEvent;
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
    public void run()
    {
        this.connect();
        this.eventManager.notiy(new NewConnectionEvent(socket));
        this.eventManager.notiy(new ThreadEvent(THREAD_EVENT_TYPE.THREAD_EVENT_FINISHED, this.getIdString()));
    }

    private void connect()
    {
        try
        {
            InetAddress addr = InetAddress.getByName(this.distantIP);
            SocketAddress socketAddr = new InetSocketAddress(addr, this.distantPort);
            this.socket.connect(socketAddr);
            Log.Info("Connection success with : " + this.distantIP + ":" + this.distantPort);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
