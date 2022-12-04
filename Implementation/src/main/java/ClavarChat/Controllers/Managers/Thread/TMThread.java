package ClavarChat.Controllers.Managers.Thread;

import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Models.Events.ThreadEvent;
import ClavarChat.Utils.Log.Log;

public class TMThread extends Thread
{
    private ThreadExecutable executable;
    private EventManager eventManager;
    private int id;

    public TMThread(int id)
    {
        this.id = id;
        this.executable = null;
        this.eventManager = EventManager.getInstance();
    }

    public TMThread(ThreadExecutable runnable, int id)
    {
        this.id = id;
        this.executable = runnable;
        this.eventManager = EventManager.getInstance();
    }

    public void setExecutable(ThreadExecutable executable)
    {
        this.executable = executable;
    }

    @Override
    public void run()
    {
        if (this.executable != null)
        {
            this.executable.run();
        }
        else
        {
            Log.Error(this.getClass().getName() + " Cannot start because runnable is null ");
        }
        Log.Print(this.getClass().getName() + " Thread : " + this.id + " finished");
        this.eventManager.notiy(new ThreadEvent(ThreadEvent.THREAD_FINISHED, this.id));
    }
}
