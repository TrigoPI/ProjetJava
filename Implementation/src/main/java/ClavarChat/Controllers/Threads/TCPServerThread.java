package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.ConnectionSuccessEvent;
import ClavarChat.Models.Events.NewConnectionEvent;
import ClavarChat.Utils.Log.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread extends NetworkThread
{
    ServerSocket serverSocket;

    public TCPServerThread(int port)
    {
        super();
        this.createServer(port);
    }

    @Override
    public void close()
    {
        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            Log.Info("TCP Server start on : " + this.localIP + ":" + this.localPort);

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

            this.localIP = this.serverSocket.getInetAddress();
            this.localPort = this.serverSocket.getLocalPort();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
