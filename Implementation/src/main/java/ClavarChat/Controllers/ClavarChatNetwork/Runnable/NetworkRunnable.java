package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Controllers.Threads.ThreadRunnable;

public abstract class NetworkRunnable implements ThreadRunnable
{
    protected NetworkManager networkManager;
    protected EventManager eventManager;

    protected NetworkRunnable(NetworkManager networkManager)
    {
        this.networkManager = networkManager;
        this.eventManager = EventManager.getInstance();
    }

    @Override
    public void run()
    {
        this.update();
    }

    protected abstract void update();
}
