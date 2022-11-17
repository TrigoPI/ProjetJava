package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.ConnectionSuccessEvent;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Loggin.Loggin;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
            this.eventManager.notiy(new ConnectionSuccessEvent(this.socket));
        }

        Loggin.Info("TCP_OUT on : " + this.socket.getLocalAddress() + ":" + this.socket.getLocalPort() + " --> " + this.ip + ":" + this.port);

        try
        {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            while (this.socket.isConnected())
            {
                if (!this.datas.isEmpty())
                {
                    Loggin.Print("Send : " + this.socket.getLocalAddress() + ":" + this.socket.getLocalPort() + " --> " + this.ip + ":" + this.port);
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
            this.socket.connect(new InetSocketAddress(this.ip, this.port));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
