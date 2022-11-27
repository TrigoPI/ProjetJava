package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

public class TcpServer extends Server
{
    public TcpServer(NetworkManager networkManager, int serverId, int port)
    {
        super(networkManager, serverId, port);
    }

    @Override
    protected void runServer()
    {
        this.networkManager.startTcpServer(this.serverId, this.port);
    }
}
