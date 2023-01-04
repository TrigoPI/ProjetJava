package ClavarChat.Controllers.Runnables.Network.TCPIN;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcListener.NetworkListener;
import ClavarChat.Models.ClvcMessage.ClvcMessage;
import ClavarChat.Models.ClvcSocket.ClvcSocket;

import java.util.concurrent.atomic.AtomicBoolean;

public class TCPIN implements TMRunnable
{
    private final ClvcSocket socket;
    private final NetworkListener listener;
    private final AtomicBoolean running;
    public TCPIN(ClvcSocket socket, NetworkListener listener)
    {
        this.socket = socket;
        this.listener = listener;
        this.running = new AtomicBoolean(true);
    }

    public void stop()
    {
        this.running.set(false);
    }

    public void run()
    {
        while (this.running.get())
        {
            ClvcMessage data = (ClvcMessage)this.socket.receive().data;
            this.listener.onPacket(this.socket.getSrcIp(), this.socket.getSrcPort(), this.socket.getDstIp(), this.socket.getDstPort(), data);
        }
    }
}
