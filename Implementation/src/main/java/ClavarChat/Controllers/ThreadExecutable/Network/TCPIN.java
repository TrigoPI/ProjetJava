package ClavarChat.Controllers.ThreadExecutable.Network;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.Events.ConnectionEvent;
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
        String dstIp = this.networkManager.getDistantSocketIp(this.socketId);
        int dstPort = this.networkManager.getDistantSocketPort(this.socketId);

        while (this.isRunning())
        {
            NetworkPaquet paquet = this.networkManager.tcpReceive(this.socketId);

            if (paquet != null)
            {
                this.eventManager.notiy(new SocketDataEvent(paquet.srcIp, dstPort, dstIp, paquet.dstPort, (ClavarChatMessage)paquet.data));
            }
            else
            {
                this.eventManager.notiy(new ConnectionEvent(ConnectionEvent.CONNECTION_FAILED, dstIp, dstPort, this.socketId));
            }
        }
    }
}
