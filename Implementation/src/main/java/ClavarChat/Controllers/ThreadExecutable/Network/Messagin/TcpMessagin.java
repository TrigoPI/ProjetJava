package ClavarChat.Controllers.ThreadExecutable.Network.Messagin;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.ThreadExecutable.Network.NetworkExecutable;

import java.util.concurrent.Semaphore;

public abstract class TcpMessagin extends NetworkExecutable
{
    protected int socketId;

    private boolean running;
    private final Semaphore runningSemaphore;

    protected TcpMessagin(NetworkManager networkManager, int socketId)
    {
        super(networkManager);

        this.running = true;
        this.socketId = socketId;
        this.runningSemaphore = new Semaphore(1);
    }

    protected boolean isRunning()
    {
        boolean running = true;

        try
        {
            this.runningSemaphore.acquire();
            running = this.running;
            this.runningSemaphore.release();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return running;
    }

    public void stop()
    {
        try
        {
            runningSemaphore.acquire();
            this.running = false;
            runningSemaphore.release();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void update()
    {
        this.runSocket();
    }

    protected abstract void runSocket();
}
