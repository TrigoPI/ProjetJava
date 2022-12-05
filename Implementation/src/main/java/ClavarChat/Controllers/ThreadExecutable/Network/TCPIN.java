package ClavarChat.Controllers.ThreadExecutable.Network;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.Events.ConnectionEvent;
import ClavarChat.Models.Events.SocketDataEvent;
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
        int dstPort = this.networkManager.getDistantSocketPort(this.socketId);

        while (this.isRunning())
        {
            NetworkPaquet paquet = this.networkManager.tcpReceive(this.socketId);

            if (paquet != null)
            {
                Log.Error(this.getClass().getName() + " New TCP paquet " + paquet.srcIp + ":" + paquet.dstPort + " <-- " + dstIp + ":" + dstPort);
                this.eventManager.notiy(new SocketDataEvent(paquet.srcIp, dstPort, dstIp, paquet.dstPort, (ClavarChatMessage)paquet.data));
            }
            else
            {
                Log.Error(this.getClass().getName() + " Error in TCP Receive " + dstIp + ":" + dstPort);
                this.eventManager.notiy(new ConnectionEvent(ConnectionEvent.CONNECTION_FAILED, dstIp, dstPort, this.socketId));
            }
        }
    }
}
