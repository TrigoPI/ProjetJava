package ClavarChat.Controllers.Runnables.Network.TCPOUT;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcListener.NetworkListener;
import ClavarChat.Models.ClvcSocket.ClvcSocket;

public class TCPOUT implements TMRunnable
{
    private final ClvcSocket socket;
    private final NetworkListener listener;
    public TCPOUT(ClvcSocket socket, NetworkListener listener)
    {
        this.socket = socket;
        this.listener = listener;
    }

    @Override
    public void run()
    {
        while (!this.socket.isClosed()) this.socket.send();
        this.listener.onConnectionFailed(this.socket.getSocketId(), this.socket.getDistantIp());
    }
}
