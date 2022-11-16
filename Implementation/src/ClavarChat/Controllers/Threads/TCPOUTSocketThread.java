package ClavarChat.Controllers.Threads;

import ClavarChat.Utils.Loggin.Loggin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPOUTSocketThread extends TCPMessaginThread
{
    public TCPOUTSocketThread(String ip, int port) throws UnknownHostException
    {
        super(ip, port);
    }

    public TCPOUTSocketThread(Socket socket)
    {
        super(socket);
    }

    @Override
    public void run()
    {
        if (!this.socket.isConnected()) this.connect();
        Loggin.Info("TCP_OUT on : " + this.socket.getLocalAddress() + ":" + this.socket.getLocalPort() + " --> " + this.ip + ":" + this.port);
    }

    private void connect()
    {
        try
        {
            this.socket.connect(new InetSocketAddress(this.ip, this.port));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
