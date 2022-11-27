package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

public class UdpServer extends Server
{
    public UdpServer(NetworkManager networkManager, int port)
    {
        super(networkManager, port);
    }

    @Override
    protected void setServerID()
    {
        this.serverID = this.networkManager.createUdpServer();
    }

    @Override
    protected void runServer()
    {

    }
}
