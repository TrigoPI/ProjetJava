package ClavarChat.Controllers.Runnables.Network.TCPOUT;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcListener.NetworkListener;
import ClavarChat.Models.ClvcSocket.ClvcSocket;
import ClavarChat.Utils.Log.Log;

import java.util.concurrent.atomic.AtomicBoolean;


public class TCPOUT implements TMRunnable
{
    private final ClvcSocket socket;
    private final NetworkListener listener;
    private final AtomicBoolean finished;

    public TCPOUT(ClvcSocket socket, NetworkListener listener)
    {
        this.socket = socket;
        this.listener = listener;
        this.finished = new AtomicBoolean(false);
    }

    public boolean isFinished()
    {
        return this.finished.get();
    }

    @Override
    public void run()
    {
        String distantIp = this.socket.getDistantIp();
        this.socket.send();
        this.finished.set(true);
        Log.Print(this.getClass().getName() + " [ " + distantIp + " ] finished");
        this.listener.onMessengerFinished(distantIp);
    }
}
