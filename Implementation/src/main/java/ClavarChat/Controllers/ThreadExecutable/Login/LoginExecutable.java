package ClavarChat.Controllers.ThreadExecutable.Login;

import ClavarChat.Controllers.Chain.Discover;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Controllers.Managers.Thread.ThreadExecutable;
import ClavarChat.Models.ChainData.Response;
import ClavarChat.Models.Events.Login.LoginEvent;

public class LoginExecutable implements ThreadExecutable
{
    private final Discover discover;
    private final EventManager eventManager;

    public LoginExecutable(Discover discover)
    {
        this.discover = discover;
        this.eventManager = EventManager.getInstance();
    }

    @Override
    public void run()
    {
        String response = this.discover.handle();

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
