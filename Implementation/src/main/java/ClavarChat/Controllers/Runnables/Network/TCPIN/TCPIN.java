package ClavarChat.Controllers.Runnables.Network.TCPIN;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcListener.NetworkListener;
import ClavarChat.Models.ClvcMessage.ClvcMessage;
import ClavarChat.Models.ClvcSocket.ClvcSocket;
import ClavarChat.Models.NetworkPaquet.NetworkPaquet;
import ClavarChat.Utils.Log.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class TCPIN implements TMRunnable
{
    private final ClvcSocket socket;
    private final NetworkListener listener;
    private final AtomicBoolean finished;

    public TCPIN(ClvcSocket socket, NetworkListener listener)
    {
        this.socket = socket;
        this.listener = listener;
        this.finished = new AtomicBoolean(false);
    }

    public boolean isFinished()
    {
        return this.finished.get();
    }

    public void run()
    {
        String distantIp = this.socket.getDistantIp();

        if (this.isCloseWait()) return;

        while (!this.socket.isClosed()) this.notify(this.socket.receive());
        this.finished.set(true);
        Log.Print(this.getClass().getName() + " [ " + distantIp + " ] finished");
        this.listener.onMessengerFinished(distantIp);
    }

    private boolean isCloseWait()
    {
        String distantIp = this.socket.getDistantIp();
        if (this.socket.getState() != ClvcSocket.SOCKET_STATE.CLOSE_WAIT) return false;
        this.finished.set(true);
        Log.Print(this.getClass().getName() + " [ " + distantIp + " ] finished");
        this.listener.onMessengerFinished(distantIp);
        return true;
    }

    private void notify(NetworkPaquet packet)
    {
        if (packet == null) return;
        this.listener.onPacket(this.socket.getDistantIp(), this.socket.getDistantPort(), (ClvcMessage)packet.data);
    }
}
