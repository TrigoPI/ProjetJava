package ClavarChat.Controllers.ClavarChatRunnable.Network.Server;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.ClavarChatListener.NetworkListener;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.NetworkPaquet.NetworkPaquet;

public class UdpServer extends Server
{
    public UdpServer(NetworkManager networkManager, NetworkListener listener, int serverId, int port)
    {
        super(networkManager, listener, serverId, port);
    }

    @Override
    protected void runServer()
    {
        int code = this.networkManager.startUdpServer(this.serverId, port);
        boolean running = true;

        if (code != 0) return;

        while (running)
        {
            NetworkPaquet paquet = this.networkManager.udpReceive(this.serverId);

            if (paquet != null)
            {
                this.listener.onData(paquet.srcIp, paquet.srcPort, paquet.dstIp, paquet.dstPort, (ClavarChatMessage)paquet.data);
            }
            else
            {
                running = false;
            }
        }

    }
}
