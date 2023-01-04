package ClavarChat.Controllers.Runnables.Network.Connection;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcListener.NetworkListener;
import ClavarChat.Models.ClvcSocket.ClvcSocket;
import ClavarChat.Utils.Log.Log;

public class TcpConnection implements TMRunnable
{
    private final ClvcSocket socket;
    private final NetworkListener listener;

    private final NetworkManager networkManager;

    public TcpConnection(ClvcSocket socket, NetworkManager networkManager, NetworkListener listener)
    {
        this.networkManager = networkManager;
        this.listener = listener;
        this.socket = socket;
    }

    @Override
    public void run()
    {
        int code = this.socket.connect();

        if (code != 0)
        {
            this.listener.onConnectionFailed(this.socket.getSocketId(), this.socket.getDstIp());
            return;
        }

        String srcIp = this.networkManager.getLocalSocketIp(this.socket.getSocketId());
        int srcPort = this.networkManager.getLocalSocketPort(this.socket.getSocketId());

        Log.Print(this.getClass().getName() + " Connection success with : " + this.socket.getDstIp() + ":" + this.socket.getDstPort());
        this.listener.onConnectionSuccess(this.socket.getDstIp());
    }
}
