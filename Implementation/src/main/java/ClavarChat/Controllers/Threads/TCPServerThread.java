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
        this.createServer(port);
    }

    @Override
    public void run()
    {
        try
        {
            Log.Info("TCP Server start on : " + this.ip + ":" + this.port);
            Socket socket = serverSocket.accept();
            this.eventManager.notiy(new NewConnectionEvent(socket));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void createServer(int port)
    {
        try
        {
            this.serverSocket = new ServerSocket(port);
            this.ip = this.serverSocket.getInetAddress();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
