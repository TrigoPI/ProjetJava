package ClavarChat.Controllers.Runnables.Network.TCPOUT;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcListener.NetworkListener;
import ClavarChat.Models.ClvcSocket.ClvcSocket;

import java.io.Serializable;

public class TCPOUT implements TMRunnable
{
    private final ClvcSocket socket;
    public TCPOUT(ClvcSocket socket)
    {
        this.socket = socket;
    }

    public void put(Serializable data)
    {
        this.socket.put(data);
    }

    @Override
    public void run()
    {
        while (!this.socket.isClosed())
        {
            this.socket.send();
        }
    }
}
