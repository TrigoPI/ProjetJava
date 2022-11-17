package ClavarChat.Controllers.Threads;

import ClavarChat.Utils.Loggin.Loggin;

import java.io.IOException;
import java.net.ServerSocket;

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
            Loggin.Info("TCP Server start on : " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
            serverSocket.accept();
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
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
