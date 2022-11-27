package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

public abstract class TcpMessagin extends NetworkRunnable
{
    protected int socketId;

    protected TcpMessagin(NetworkManager networkManager, int socketId)
    {
        super(networkManager);
        this.socketId = socketId;
    }

    @Override
    protected void update()
    {
        this.runSocket();
    }

    protected abstract void runSocket();
}
