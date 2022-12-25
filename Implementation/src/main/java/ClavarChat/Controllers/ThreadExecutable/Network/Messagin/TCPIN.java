package ClavarChat.Controllers.ThreadExecutable.Network.Messagin;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.Events.Network.ConnectionEvent;
import ClavarChat.Models.Events.Network.SocketDataEvent;
import ClavarChat.Models.NetworkPaquet.NetworkPaquet;
import ClavarChat.Utils.Log.Log;

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
        String srcIp = this.networkManager.getLocalSocketIp(this.socketId);
        int dstPort = this.networkManager.getDistantSocketPort(this.socketId);
        int srcPort = this.networkManager.getLocalSocketPort(this.socketId);

        while (this.isRunning())
        {
            NetworkPaquet paquet = this.networkManager.tcpReceive(this.socketId);

            if (paquet != null)
            {
                Log.Info(this.getClass().getName() + " New TCP paquet " + srcIp + ":" + srcPort + " <-- " + dstIp + ":" + dstPort);
                this.eventManager.notify(new SocketDataEvent(dstIp, dstPort, srcIp, srcPort, (ClavarChatMessage)paquet.data));
            }
            else
            {
                Log.Error(this.getClass().getName() + " Error in TCP Receive " + dstIp + ":" + dstPort);
                this.eventManager.notify(new ConnectionEvent(ConnectionEvent.CONNECTION_FAILED, dstIp, dstPort, this.socketId));
            }
        }
    }
}
