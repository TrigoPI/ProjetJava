package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

import java.io.IOException;

public class TcpConnection extends NetworkRunnable
{
    private int socketId;
    private String ip;
    private int port;

    public TcpConnection(NetworkManager networkManager, String ip, int port)
    {
        super(networkManager);
        this.socketId = this.networkManager.createSocket();
        this.port = port;
        this.ip = ip;
    }

    @Override
    protected void update()
    {
        try
        {
            this.networkManager.connect(this.socketId, this.ip, this.port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
