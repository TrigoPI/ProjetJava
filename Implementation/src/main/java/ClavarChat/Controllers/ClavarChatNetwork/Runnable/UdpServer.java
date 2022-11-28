package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Events.SocketDataEvent;

import java.io.Serializable;

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
                Serializable data = this.networkManager.udpReceive(this.serverId);

                if (data != null)
                {
//                    this.eventManager.notiy(new SocketDataEvent());
                }
            }
        }
    }
}
