package ClavarChat.Controllers.Runnables.Network.TCPIN;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcListener.NetworkListener;
import ClavarChat.Models.ClvcNetworkMessage.ClvcNetworkMessage;
import ClavarChat.Models.ClvcSocket.ClvcSocket;
import ClavarChat.Controllers.Managers.Network.NetworkPacket;
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
        String localIp   = this.socket.getLocalIp();
        int distantPort  = this.socket.getDistantPort();
        int localPort    = this.socket.getLocalPort();

        Log.Print(this.getClass().getName() + " Starting TCPIN : " + localIp + ":" + localPort + " <-- " + distantIp + ":" + distantPort);

        while (!this.socket.isClosed() && !this.isCloseWait()) this.notify(this.socket.receive());
        this.finished.set(true);

        Log.Print(this.getClass().getName() + " [ " + distantIp + " ] finished");
        this.listener.onMessengerFinished(distantIp);
    }

    private boolean isCloseWait()
    {
        return this.socket.getState() == ClvcSocket.SOCKET_STATE.CLOSE_WAIT;
    }

    private void notify(NetworkPacket packet)
    {
        if (packet == null) return;
        this.listener.onPacket(this.socket.getDistantIp(), this.socket.getDistantPort(), (ClvcNetworkMessage)packet.data);
    }
}
