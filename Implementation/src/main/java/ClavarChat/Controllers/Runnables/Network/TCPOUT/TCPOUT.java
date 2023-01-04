package ClavarChat.Controllers.Runnables.Network.TCPOUT;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcListener.NetworkListener;
import ClavarChat.Models.ClvcSocket.ClvcSocket;

import java.io.Serializable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPOUT implements TMRunnable
{
    private final ClvcSocket socket;
    private final NetworkListener listener;
    private final PriorityBlockingQueue<Serializable> buffer;
    private final AtomicBoolean running;
    private boolean sending;

    public TCPOUT(ClvcSocket socket, NetworkListener listener)
    {
        this.socket = socket;
        this.listener = listener;
        this.buffer = new PriorityBlockingQueue<>();
        this.running = new AtomicBoolean(true);
        this.sending = false;
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
            this.send();
            this.isSending();
        }
    }

    private void send()
    {
        if (!this.buffer.isEmpty()) this.socket.send(this.buffer.poll());
    }

    private void isSending()
    {
        if (buffer.isEmpty() && this.sending)
        {
            this.listener.onFinishedSending(this.socket.getSocketId(), this.socket.getDstIp());
            this.sending = false;
        }
        else
        {
            this.sending = true;
        }
    }
}
