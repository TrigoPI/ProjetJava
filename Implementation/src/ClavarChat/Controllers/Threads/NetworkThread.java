package ClavarChat.Controllers.Threads;

import ClavarChat.Controllers.Managers.EventManager;

abstract class NetworkThread extends Thread
{
    protected EventManager eventManager;

    protected NetworkThread()
    {
        super();
        this.eventManager = EventManager.getInstance();
    }

    abstract void close();
}
