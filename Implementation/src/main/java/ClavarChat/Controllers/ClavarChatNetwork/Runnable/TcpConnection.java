package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

public class TcpConnection extends NetworkRunnable
{
    private String ip;
    private int port;
    private int socketId;

    public TcpConnection(NetworkManager networkManager, int socketId, String ip, int port)
    {
        super(networkManager);
        this.socketId = socketId;
        this.port = port;
        this.ip = ip;
    }

    @Override
    protected void update()
    {
        this.networkManager.connect(this.socketId, this.ip, this.port);
    }
}
