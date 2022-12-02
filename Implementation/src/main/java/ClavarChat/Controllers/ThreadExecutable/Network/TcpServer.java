package ClavarChat.Controllers.ThreadExecutable.Network;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.Events.ConnectionEvent;
import ClavarChat.Models.Events.ConnectionEvent.CONNECTION_STATUS;

public class TcpServer extends Server
{
    public TcpServer(NetworkManager networkManager, int serverId, int port)
    {
        super(networkManager, serverId, port);
    }

    @Override
    protected void runServer()
    {
        int code = this.networkManager.startTcpServer(this.serverId, this.port);

        if (code == 0)
        {
            while (true)
            {
                int socketId = this.networkManager.accept(this.serverId);

                if (socketId >= 0)
                {
                    String srcIp = this.networkManager.getLocalSocketIp(socketId);
                    String dstIp = this.networkManager.getDistantSocketIp(socketId);

                    int srcPort = this.networkManager.getLocalSocketPort(socketId);
                    int dstPort = this.networkManager.getDistantSocketPort(socketId);

                    this.eventManager.notiy(new ConnectionEvent(CONNECTION_STATUS.SUCCESS, dstIp, dstPort, srcIp, srcPort, socketId));
                }
            }
        }

    }
}
