package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.ConnectionEvent;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;
import ClavarChat.Models.Events.ConnectionEvent.CONNECTION_STATUS;
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
    protected void runServer()
    {
        try
        {
            Log.Info(this.getClass().getName() + " start on : " + this.port);

            if (this.serverSocket != null)
            {
                while (true)
                {
                    Socket socket = serverSocket.accept();
                    String ip = NetworkUtils.inetAddressToString(socket.getInetAddress());
                    int port = socket.getPort();
                    this.eventManager.notiy(new ConnectionEvent(CONNECTION_STATUS.SUCCESS, ip, port, socket));
                }
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
