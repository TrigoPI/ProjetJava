package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPOUT extends TcpMessagin
{
    private LinkedBlockingQueue<Serializable> datas;

    public TCPOUT(NetworkManager networkManager, int socketId)
    {
        super(networkManager, socketId);
        this.datas = new LinkedBlockingQueue<Serializable>();
    }

    public void put(Serializable data)
    {
        try
        {
            this.datas.put(data);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void runSocket()
    {
        try
        {
            while (true)
            {
                if (!this.datas.isEmpty())
                {
                    Serializable data = this.datas.poll();
                    this.networkManager.tcpSend(socketId, data);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
