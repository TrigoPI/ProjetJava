package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.Events.SocketDataEvent;
import ClavarChat.Models.NetworkPaquet.NetworkPaquet;

public class UdpServer extends Server
{
    public UdpServer(NetworkManager networkManager, int serverId, int port)
    {
        super(networkManager, serverId, port);
    }

    @Override
    protected void runServer()
    {
        int code = this.networkManager.startUdpServer(this.serverId, port);

        if (code == 0)
        {
            while (true)
            {
                NetworkPaquet paquet = this.networkManager.udpReceive(this.serverId);

                if (paquet != null)
                {
                    this.eventManager.notiy(new SocketDataEvent(paquet.dstIp, paquet.dstPort, (ClavarChatMessage)paquet.data));
                }
            }
        }
    }
}
