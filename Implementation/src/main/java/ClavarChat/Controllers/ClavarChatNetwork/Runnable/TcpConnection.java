package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Events.ConnectionEvent;

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
        int code = this.networkManager.connect(this.socketId, this.ip, this.port);

        if (code == 0)
        {
            String srcIp = this.networkManager.getLocalSocketIp(this.socketId);
            int srcPort = this.networkManager.getLocalSocketPort(this.socketId);

            this.eventManager.notiy(new ConnectionEvent(ConnectionEvent.CONNECTION_STATUS.SUCCESS, this.ip, this.port, srcIp, srcPort, this.socketId));
        }
        else
        {
            this.eventManager.notiy(new ConnectionEvent(ConnectionEvent.CONNECTION_STATUS.FAILED, this.ip, this.port, this.socketId));
        }
    }
}
