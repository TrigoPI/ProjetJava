package ClavarChat.Models.ClvcMessenger;

import ClavarChat.Controllers.Runnables.Network.TCPIN.TCPIN;
import ClavarChat.Controllers.Runnables.Network.TCPOUT.TCPOUT;
import ClavarChat.Models.ClvcSocket.ClvcSocket;
import ClavarChat.Utils.Clock.Clock;

import java.util.concurrent.atomic.AtomicReference;

public class ClvcMessenger
{
    public final ClvcSocket socket;
    public final TCPIN in;
    public final TCPOUT out;
    private final AtomicReference<Clock> clock;

    public ClvcMessenger(ClvcSocket socket, TCPIN in, TCPOUT out)
    {
        this.socket = socket;
        this.in  = in;
        this.out = out;
        this.clock = new AtomicReference<>(new Clock());
    }

    public double getTime()
    {
        return this.clock.get().timeSecond();
    }

    public void resetTimer()
    {
        this.clock.get().resetSecond();
    }
}
