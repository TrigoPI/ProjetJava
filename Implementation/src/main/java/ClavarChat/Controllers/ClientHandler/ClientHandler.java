package ClavarChat.Controllers.ClientHandler;

import ClavarChat.Controllers.Threads.TCPINSocketThread;
import ClavarChat.Controllers.Threads.TCPOUTSocketThread;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

public class ClientHandler
{
    private TCPINSocketThread in;
    private TCPOUTSocketThread out;
    private Socket socket;

    private String distantIP;
    private String localIP;
    private int distantPort;
    private int localPort;

    public ClientHandler(Socket socket, TCPINSocketThread in, TCPOUTSocketThread out)
    {
        this.socket = socket;

        this.in = in;
        this.out = out;

        this.distantIP = NetworkUtils.inetAddressToString(socket.getInetAddress());
        this.localIP = NetworkUtils.inetAddressToString(socket.getLocalAddress());
        this.distantPort = socket.getPort();
        this.localPort = socket.getLocalPort();
    }

    public int getDistantPort()
    {
        return distantPort;
    }

    public int getLocalPort()
    {
        return localPort;
    }

    public String getDistantIP()
    {
        return distantIP;
    }

    public String getLocalIP()
    {
        return localIP;
    }

    public void send(Serializable data)
    {
        this.out.send(data);
    }

    public void stop()
    {
        try
        {
            this.socket.close();
            this.in.stopSocket();
            this.out.stopSocket();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
