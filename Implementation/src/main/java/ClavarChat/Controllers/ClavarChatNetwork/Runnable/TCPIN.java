package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.Events.ConnectionEvent;
import ClavarChat.Models.Events.ConnectionEvent.CONNECTION_STATUS;
import ClavarChat.Models.Events.SocketDataEvent;
import ClavarChat.Models.NetworkPaquet.NetworkPaquet;

public class TCPIN extends TcpMessagin
{

    public TCPIN(NetworkManager networkManager, int socketID)
    {
        super(networkManager, socketID);
    }

    @Override
    protected void runSocket()
    {
        while (this.isRunning())
        {
            NetworkPaquet paquet = this.networkManager.tcpReceive(this.socketId);

            if (paquet != null)
            {
                this.eventManager.notiy(new SocketDataEvent(paquet.dstIp, paquet.dstPort, (ClavarChatMessage)paquet.data));
            }
            else
            {
                this.eventManager.notiy(new ConnectionEvent(CONNECTION_STATUS.FAILED, paquet.dstIp, paquet.dstPort, this.socketId));
            }
        }
    }
}
