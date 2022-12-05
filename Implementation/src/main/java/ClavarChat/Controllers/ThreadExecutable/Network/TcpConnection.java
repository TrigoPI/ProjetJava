package ClavarChat.Controllers.ThreadExecutable.Network;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.Events.ConnectionEvent;
import ClavarChat.Utils.Log.Log;

public class TcpConnection extends NetworkExecutable
{
    private final String ip;
    private final int port;
    private final int socketId;

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

            Log.Print(this.getClass().getName() + " Connection success with : " + this.ip + ":" + this.port);
            this.eventManager.notiy(new ConnectionEvent(ConnectionEvent.CONNECTION_SUCCESS, this.ip, this.port, srcIp, srcPort, this.socketId));
        }
        else
        {
            Log.Warning(this.getClass().getName() + " Connection failed with : " + this.ip + ":" + this.port);
            this.eventManager.notiy(new ConnectionEvent(ConnectionEvent.CONNECTION_FAILED, this.ip, this.port, this.socketId));
        }
    }
}
