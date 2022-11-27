package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

import java.util.concurrent.Semaphore;

public abstract class TcpMessagin extends NetworkRunnable
{
    protected int socketId;
    private boolean running;
    private Semaphore semaphore;

    protected TcpMessagin(NetworkManager networkManager, int socketId)
    {
        super(networkManager);
        this.semaphore = new Semaphore(1);
        this.running = true;
        this.socketId = socketId;
    }

    protected boolean isRunning()
    {
        boolean running = true;
        try
        {
            semaphore.acquire();
            running = this.running;
            semaphore.release();
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
            semaphore.acquire();
            this.running = false;
            semaphore.release();
        }
        catch (InterruptedException e) {
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
