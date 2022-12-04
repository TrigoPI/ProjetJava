package GUI.GUIControllers;

import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Models.Events.LoginEvent;
import ClavarChat.Utils.Log.Log;
import GUI.GUIControllers.Controllers.LoginController;
import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Models.Events.Event;

public class GUIControllers implements Listener
{
    private final LoginController loginController;
    private EventManager eventManager;

    public GUIControllers(LoginController loginController)
    {
        this.loginController = loginController;
        this.eventManager = EventManager.getInstance();

        eventManager.addListenner(this, LoginEvent.LOGIN_SUCCESS);
        eventManager.addListenner(this, LoginEvent.LOGIN_FAILED);
    }

    @Override
    public void onEvent(Event event)
    {
        switch (event.type)
        {
            case LoginEvent.LOGIN_SUCCESS:
                this.onLoginSucces();
                break;
        }
    }

    private void onLoginSucces()
    {
        this.loginController.onLoginSuccess();
    }
}
