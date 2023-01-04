package ClavarChat.Controllers.Runnables.Network.Server;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcListener.NetworkListener;

public abstract class Server implements TMRunnable
{
    protected int port;
    protected int serverId;
    protected NetworkManager networkManager;
    protected NetworkListener listener;

    protected Server(NetworkManager networkManager, NetworkListener listener, int serverId, int port)
    {
        this.port = port;
        this.serverId = serverId;
        this.networkManager = networkManager;
        this.listener = listener;
    }

    public void run()
    {
        this.runServer();
    }

    protected abstract void runServer();
}
