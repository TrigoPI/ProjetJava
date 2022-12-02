package ClavarChat.Controllers.ThreadExecutable.Network;

import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.Managers.Thread.ThreadExecutable;

public abstract class NetworkExecutable implements ThreadExecutable
{
    protected NetworkManager networkManager;
    protected EventManager eventManager;

    protected NetworkExecutable(NetworkManager networkManager)
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
