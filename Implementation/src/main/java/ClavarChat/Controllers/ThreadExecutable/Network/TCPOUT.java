package ClavarChat.Controllers.ThreadExecutable.Network;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.Events.ConnectionEvent;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPOUT extends TcpMessagin
{
    private final LinkedBlockingQueue<Serializable> datas;

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
        String dstIp = this.networkManager.getDistantSocketIp(this.socketId);
        int dstPort = this.networkManager.getDistantSocketPort(this.socketId);

        while (this.isRunning())
        {
            if (!this.datas.isEmpty())
            {
                Serializable data = this.datas.poll();

                int code = this.networkManager.tcpSend(socketId, data);

                if (code == -1)
                {
                    this.eventManager.notiy(new ConnectionEvent(ConnectionEvent.CONNECTION_FAILED, dstIp, dstPort, this.socketId));
                }
            }
        }
    }
}
