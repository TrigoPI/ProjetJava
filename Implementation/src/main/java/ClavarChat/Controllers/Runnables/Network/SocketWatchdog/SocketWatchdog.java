package ClavarChat.Controllers.Runnables.Network.SocketWatchdog;

import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcMessenger.ClvcMessenger;
import ClavarChat.Utils.Log.Log;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketWatchdog implements TMRunnable
{
    private final List<ClvcMessenger> messengers;
    private final AtomicBoolean running;

    public SocketWatchdog()
    {
        this.messengers = Collections.synchronizedList(new CopyOnWriteArrayList<>());
        this.running = new AtomicBoolean(true);
    }

    public void addMessenger(ClvcMessenger messenger)
    {
        Log.Print(this.getClass().getName() + " Observing socket " + messenger.socket.getSocketId());
        this.messengers.add(messenger);
    }

    public void stop()
    {
        this.running.set(false);
    }

    @Override
    public void run()
    {
        int timeout = 10;

        while (this.running.get())
        {
            for (ClvcMessenger messenger : this.messengers)
            {
                if (messenger.getTime() > timeout)
                {
                    Log.Print(this.getClass().getName() + " Closing socket : " + messenger.socket.getSocketId() + " , no interaction");
                    messenger.socket.close();
                    this.messengers.remove(messenger);
                }

                if (messenger.socket.isClosed())
                {
                    Log.Print(this.getClass().getName() + " removing socket : " + messenger.socket.getSocketId());
                    this.messengers.remove(messenger);
                }
            }

            this.sleep();
        }
    }

    private void sleep()
    {
        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
