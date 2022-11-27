package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

public abstract class Server extends NetworkRunnable
{
    protected int port;
    protected int serverId;

    protected Server(NetworkManager networkManager, int serverId, int port)
    {
        super(networkManager);
        this.port = port;
        this.serverId = serverId;
    }

    @Override
    protected void update()
    {
        this.runServer();
    }

    protected abstract void runServer();
}
