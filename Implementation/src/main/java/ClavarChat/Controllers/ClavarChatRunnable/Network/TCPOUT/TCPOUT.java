package ClavarChat.Controllers.ClavarChatRunnable.Network.TCPOUT;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClavarChatListener.NetworkListener;
import ClavarChat.Models.ClavarChatSocket.ClavarChatSocket;

import java.io.Serializable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPOUT implements TMRunnable
{
    private final ClavarChatSocket socket;
    private final NetworkListener listener;
    private final PriorityBlockingQueue<Serializable> buffer;
    private final AtomicBoolean running;

    public TCPOUT(ClavarChatSocket socket, NetworkListener listener)
    {
        this.socket = socket;
        this.listener = listener;
        this.buffer = new PriorityBlockingQueue<>();
        this.running = new AtomicBoolean(true);
    }

    public void stop()
    {
        this.running.set(false);
    }

    public void put(Serializable data)
    {
        this.buffer.put(data);
    }

    @Override
    public void run()
    {
        while (this.running.get())
        {
            if (!this.buffer.isEmpty())
            {
                this.socket.send(this.buffer.poll());
            }
        }
    }
}
