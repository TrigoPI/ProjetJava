package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Log.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class TCPOUTSocketThread extends TCPMessaginThread
{
    private LinkedList<Paquet> datas;

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
        Log.Info("TCP_OUT RUN : " + this.getLocalIP() + ":" + this.localPort + " --> " + this.getDistantIP() + ":" + this.getDistantPort());
        this.update();
    }

    private void update()
    {
        try
        {
            while (this.socket.isConnected())
            {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                this.sendingLoop(out);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void sendingLoop(ObjectOutputStream out) throws IOException
    {
        if (!this.datas.isEmpty())
        {
            Log.Info("Send Data : " + this.getLocalIP() + ":" + this.localPort + " --> " + this.getDistantIP() + ":" + this.getDistantPort());
            out.writeObject(this.datas.pop());
        }
    }
}
