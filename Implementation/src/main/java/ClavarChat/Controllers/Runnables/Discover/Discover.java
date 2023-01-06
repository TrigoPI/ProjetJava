package ClavarChat.Controllers.Runnables.Discover;

import ClavarChat.Controllers.API.EventAPI;
import ClavarChat.Controllers.Handlers.DiscoverHandler;
import ClavarChat.Controllers.Managers.Thread.TMRunnable;
import ClavarChat.Models.ClvcEvent.Login.LoginEvent;

public class Discover implements TMRunnable
{
    private final DiscoverHandler discoverHandler;
    public EventAPI eventAPI;

    public Discover(DiscoverHandler discoverHandler, EventAPI eventAPI)
    {
        this.discoverHandler = discoverHandler;
        this.eventAPI = eventAPI;
    }

    @Override
    public void run()
    {
        boolean success = this.discoverHandler.discover();

        if (success)
        {
            this.eventAPI.notify(new LoginEvent(LoginEvent.LOGIN_SUCCESS));
        }
        else
        {
            this.eventAPI.notify(new LoginEvent(LoginEvent.LOGIN_FAILED));
        }
    }
}
