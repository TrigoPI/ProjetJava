package ClavarChat.Controllers.Threads;

import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Models.Events.ThreadEvent;

public abstract class NetworkThread extends Thread
{
    protected EventManager eventManager;

    protected NetworkThread()
    {
        this.eventManager = EventManager.getInstance();
    }

    protected abstract void update();

    public String getIdString()
    {
        return "" + this.getId();
    }

    @Override
    public void run()
    {
        this.update();
        this.eventManager.notiy(new ThreadEvent(ThreadEvent.THREAD_STATUS.FINISHED, this.getIdString()));
    }
}
