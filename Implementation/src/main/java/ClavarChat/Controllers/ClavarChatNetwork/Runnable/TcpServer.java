package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

import java.io.IOException;

public class TcpServer extends Server
{
    public TcpServer(NetworkManager networkManager, int port)
    {
        super(networkManager, port);
    }

    @Override
    protected void setServerID()
    {
        this.serverID = this.networkManager.createTcpServer();
    }

    @Override
    protected void runServer()
    {
        try
        {
            this.networkManager.startTcpServer(this.serverID, this.port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
