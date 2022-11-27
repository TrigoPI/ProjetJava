package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

public abstract class Server extends NetworkRunnable
{
    protected int port;
    protected int serverID;

    protected Server(NetworkManager networkManager, int port)
    {
        super(networkManager);
        this.port = port;
    }

    @Override
    protected void update()
    {
        this.setServerID();
        this.runServer();
    }

    protected abstract void setServerID();
    protected abstract void runServer();
}
