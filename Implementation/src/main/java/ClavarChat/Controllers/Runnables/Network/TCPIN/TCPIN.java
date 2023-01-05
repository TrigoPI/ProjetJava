package ClavarChat.Controllers.Runnables.Network.TCPIN;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcListener.NetworkListener;
import ClavarChat.Models.ClvcMessage.ClvcMessage;
import ClavarChat.Models.ClvcSocket.ClvcSocket;
import ClavarChat.Models.NetworkPaquet.NetworkPaquet;

public class TCPIN implements TMRunnable
{
    private final ClvcSocket socket;
    private final NetworkListener listener;
    public TCPIN(ClvcSocket socket, NetworkListener listener)
    {
        this.socket = socket;
        this.listener = listener;
    }

    public void run()
    {
        String distantIp = this.socket.getDistantIp();

        while (!this.socket.isClosed()) this.notify(this.socket.receive());
        this.listener.onConnectionFailed(this.socket.getSocketId(), distantIp);
    }

    private void notify(NetworkPaquet packet)
    {
        if (packet == null) return;
        this.listener.onPacket(this.socket.getSrcIp(), this.socket.getSrcPort(), this.socket.getDstIp(), this.socket.getDstPort(), (ClvcMessage)packet.data);
    }
}
