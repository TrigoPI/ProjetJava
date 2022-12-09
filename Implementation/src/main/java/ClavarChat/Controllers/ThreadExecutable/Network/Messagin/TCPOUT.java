package ClavarChat.Controllers.ThreadExecutable.Network.Messagin;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.Events.Network.ConnectionEvent;
import ClavarChat.Models.Events.Network.SocketSendingEndEvent;
import ClavarChat.Utils.Log.Log;

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

        boolean isSending = false;

        while (this.isRunning())
        {
            if (!this.datas.isEmpty())
            {
                isSending = true;

                Serializable data = this.datas.poll();
                int code = this.networkManager.tcpSend(socketId, data);

                if (code == -1)
                {
                    Log.Error(this.getClass().getName() + " Error in TCP Send " + dstIp + ":" + dstPort);
                    this.eventManager.notiy(new ConnectionEvent(ConnectionEvent.CONNECTION_FAILED, dstIp, dstPort, this.socketId));
                }
            }
            else
            {
                if (isSending)
                {
                    this.eventManager.notiy(new SocketSendingEndEvent(this.socketId));
                    isSending = false;
                }
            }

        }
    }
}
