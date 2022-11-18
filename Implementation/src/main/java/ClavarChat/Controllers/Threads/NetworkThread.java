package ClavarChat.Controllers.Threads;

import ClavarChat.Controllers.Managers.EventManager;

public class NetworkThread extends Thread
{
    protected EventManager eventManager;

    protected NetworkThread()
    {
        this.eventManager = EventManager.getInstance();
    }

    public String getIdString()
    {
        return "" + this.getId();
    }
}
