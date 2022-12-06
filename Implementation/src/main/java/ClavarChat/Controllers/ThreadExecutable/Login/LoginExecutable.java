package ClavarChat.Controllers.ThreadExecutable.Login;

import ClavarChat.Controllers.Chain.Discover;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Controllers.Managers.Thread.ThreadExecutable;
import ClavarChat.Models.ChainData.Request.Request;
import ClavarChat.Models.ChainData.Response.Response;
import ClavarChat.Models.Events.Login.LoginEvent;

public class LoginExecutable implements ThreadExecutable
{
    private final Discover discover;
    private final Request request;
    private final EventManager eventManager;

    public LoginExecutable(Discover discover, Request request)
    {
        this.discover = discover;
        this.request = request;
        this.eventManager = EventManager.getInstance();
    }

    @Override
    public void run()
    {
        String response = this.discover.handle(request);

        if (response == Response.DISCOVER_ERROR || response == Response.INVALID_PSEUDO)
        {
            this.eventManager.notiy(new LoginEvent(LoginEvent.LOGIN_FAILED));
        }
        else
        {
            this.eventManager.notiy(new LoginEvent(LoginEvent.LOGIN_SUCCESS));
        }
    }
}
