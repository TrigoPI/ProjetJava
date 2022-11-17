package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.NewConnectionEvent;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Log.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class TCPOUTSocketThread extends TCPMessaginThread
{
    private LinkedList<Paquet> datas;

    public TCPOUTSocketThread(String ip, int port) throws UnknownHostException
    {
        super(ip, port);
        this.datas = new LinkedList<Paquet>();
    }

    public TCPOUTSocketThread(Socket socket)
    {
        super(socket);
        this.datas = new LinkedList<Paquet>();
    }

    public void send(Paquet paquet)
    {
        this.datas.push(paquet);
    }

    @Override
    public void run()
    {
        if (!this.socket.isConnected())
        {
            this.connect();
            this.localIP = this.socket.getLocalAddress();
            this.localPort = this.socket.getLocalPort();
            this.eventManager.notiy(new NewConnectionEvent(this.socket));
        }

        Log.Info("TCP_OUT RUN : " + this.getLocalIP() + ":" + this.localPort + " --> " + this.getDistantIP() + ":" + this.getDistantPort());

        try
        {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            while (this.socket.isConnected())
            {
                if (!this.datas.isEmpty())
                {
                    Log.Info("Send Data : " + this.getLocalIP() + ":" + this.localPort + " --> " + this.getDistantIP() + ":" + this.getDistantPort());
                    out.writeObject(this.datas.pop());
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void connect()
    {
        try
        {
            this.socket.connect(new InetSocketAddress(this.distantIP, this.distantPort));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
