package ClavarChat.Controllers.Runnables.Network.Server;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.ClvcListener.NetworkListener;

public class TcpServer extends Server
{
    public TcpServer(NetworkManager networkManager, NetworkListener listener, int serverId, int port)
    {
        super(networkManager, listener, serverId, port);
    }

    @Override
    protected void runServer()
    {
        int code = this.networkManager.startTcpServer(this.serverId, this.port);
        boolean running = true;

        if (code != 0) return;

        while (running)
        {
            int socketId = this.networkManager.accept(this.serverId);

            if (socketId >= 0)
            {
                String srcIp = this.networkManager.getDistantSocketIp(socketId);
                String dstIp = this.networkManager.getLocalSocketIp(socketId);

                int srcPort = this.networkManager.getDistantSocketPort(socketId);
                int dstPort = this.networkManager.getLocalSocketPort(socketId);

                this.listener.onNewConnection(socketId, srcIp, srcPort, dstIp, dstPort);
            }
            else
            {
                running = false;
            }
        }

    }
}
