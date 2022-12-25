package ClavarChat.Controllers.ThreadExecutable.Network.Server;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.Events.Network.SocketDataEvent;
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
        boolean running = true;

        if (code == 0)
        {
            while (running)
            {
                NetworkPaquet paquet = this.networkManager.udpReceive(this.serverId);

                if (paquet != null)
                {
                    this.eventManager.notify(new SocketDataEvent(paquet.srcIp, paquet.srcPort, paquet.dstIp, paquet.dstPort, (ClavarChatMessage)paquet.data));
                }
                else
                {
                    running = false;
                }
            }
        }
    }
}
