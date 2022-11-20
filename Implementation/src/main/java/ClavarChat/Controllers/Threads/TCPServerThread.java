package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.SuccessConectionEvent;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;

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
    protected void update()
    {
        try
        {
            Log.Info(this.getClass().getName() + " start on : " + this.port);

            while (true)
            {
                Socket socket = serverSocket.accept();
                this.eventManager.notiy(new SuccessConectionEvent(socket, NetworkUtils.inetAddressToString(socket.getInetAddress()), socket.getPort()));
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    protected void createServer(int port)
    {
        try
        {
            this.serverSocket = new ServerSocket(port);
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}
