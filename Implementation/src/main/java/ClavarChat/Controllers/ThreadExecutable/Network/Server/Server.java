package ClavarChat.Controllers.ThreadExecutable.Network.Server;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.ThreadExecutable.Network.NetworkExecutable;

public abstract class Server extends NetworkExecutable
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
