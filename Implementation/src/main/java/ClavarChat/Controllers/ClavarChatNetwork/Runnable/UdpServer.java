package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

import java.io.IOException;

public class UdpServer extends Server
{
    public UdpServer(NetworkManager networkManager, int serverId, int port)
    {
        super(networkManager, serverId, port);
    }

    @Override
    protected void runServer()
    {
        try
        {
            this.networkManager.startUdpServer(this.serverId, port);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
