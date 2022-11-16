package ClavarChat.Controllers.Threads;

import ClavarChat.Controllers.Managers.EventManager;

public abstract class NetworkThread extends Thread
{
    protected EventManager eventManager;

    protected NetworkThread()
    {
        super();
        this.eventManager = EventManager.getInstance();
    }

    public String getIdString()
    {
        return "" + this.getId();
    }

    abstract public void close();
}
