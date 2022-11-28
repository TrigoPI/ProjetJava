package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Events.ConnectionEvent;
import ClavarChat.Models.Events.ConnectionEvent.CONNECTION_STATUS;
import ClavarChat.Models.Events.SocketDataEvent;

import java.io.Serializable;

public class TCPIN extends TcpMessagin
{

    public TCPIN(NetworkManager networkManager, int socketID)
    {
        super(networkManager, socketID);
    }

    @Override
    protected void runSocket()
    {
        String dstIp = this.networkManager.getDistantSocketIp(this.socketId);
        int dstPort = this.networkManager.getDistantSocketPort(this.socketId);

        while (this.isRunning())
        {
            Serializable data = this.networkManager.tcpReceive(this.socketId);

            if (data != null)
            {
                this.eventManager.notiy(new SocketDataEvent(dstIp, dstPort, data));
            }
            else
            {
                this.eventManager.notiy(new ConnectionEvent(CONNECTION_STATUS.FAILED, dstIp, dstPort, this.socketId));
            }
        }
    }
}
