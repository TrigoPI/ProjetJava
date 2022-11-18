package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.NewConnectionEvent;
import ClavarChat.Utils.Log.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread extends ServerThread
{
    private ServerSocket serverSocket;

    public TCPServerThread(int port)
    {
        super(port);
    }

    @Override
    public void run()
    {
        try
        {
            Log.Info(this.getClass().getName() + " start on : " + this.port);

            while (true)
            {
                Socket socket = serverSocket.accept();
                this.eventManager.notiy(new NewConnectionEvent(socket));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected void createServer(int port)
    {
        try
        {
            this.serverSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
