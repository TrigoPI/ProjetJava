package ClavarChat.Controllers.Threads;

import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Models.Events.Enums.THREAD_EVENT_TYPE;
import ClavarChat.Models.Events.ThreadEvent;

public abstract class NetworkThread extends Thread
{
    protected EventManager eventManager;

    protected NetworkThread()
    {
        this.eventManager = EventManager.getInstance();
    }

    public String getIdString()
    {
        return "" + this.threadId();
    }

    @Override
    public void run()
    {
        this.update();
        this.eventManager.notiy(new ThreadEvent(THREAD_EVENT_TYPE.THREAD_EVENT_FINISHED, this.getIdString()));
    }

    protected abstract void update();
}
