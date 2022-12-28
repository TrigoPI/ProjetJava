package ClavarChat.Controllers.ClavarChatRunnable.Network.TCPIN;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClavarChatListener.NetworkListener;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.ClavarChatSocket.ClavarChatSocket;

import java.util.concurrent.atomic.AtomicBoolean;

public class TCPIN implements TMRunnable
{
    private final ClavarChatSocket socket;
    private final NetworkListener listener;
    private final AtomicBoolean running;
    public TCPIN(ClavarChatSocket socket, NetworkListener listener)
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
            ClavarChatMessage data = (ClavarChatMessage)this.socket.receive();
            this.listener.onData(this.socket.getSrcIp(), this.socket.getSrcPort(), this.socket.getDstIp(), this.socket.getDstPort(), data);
        }
    }
}
