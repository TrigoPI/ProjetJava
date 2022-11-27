package ClavarChat.Controllers.Threads;

import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Models.Events.ThreadEvent;
import ClavarChat.Utils.Log.Log;

public class TMThread extends Thread
{
    private ThreadRunnable runnable;
    private EventManager eventManager;
    private int id;

    public TMThread(int id)
    {
        this.id = id;
        this.runnable = null;
        this.eventManager = EventManager.getInstance();
    }

    public TMThread(ThreadRunnable runnable, int id)
    {
        this.id = id;
        this.runnable = runnable;
        this.eventManager = EventManager.getInstance();
    }

    public void setRunnable(ThreadRunnable runnable)
    {
        this.runnable = runnable;
    }

    @Override
    public void run()
    {
        if (this.runnable != null)
        {
            this.runnable.run();
        }
        else
        {
            Log.Error(this.getClass().getName() + " Cannot start because runnable is null ");
        }
        Log.Print(this.getClass().getName() + " Thread : " + this.id + " finished");
        this.eventManager.notiy(new ThreadEvent(ThreadEvent.THREAD_STATUS.FINISHED, this.id));
    }
}
