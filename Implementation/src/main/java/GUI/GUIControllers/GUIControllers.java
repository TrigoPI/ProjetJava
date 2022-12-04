package GUI.GUIControllers;

import ClavarChat.ClavarChatAPI;
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
    private ClavarChatAPI api;

    public GUIControllers(ClavarChatAPI api, LoginController loginController)
    {
        this.api = api;
        this.loginController = loginController;
        this.eventManager = EventManager.getInstance();

        eventManager.addListenner(this, LoginEvent.LOGIN_SUCCESS);
        eventManager.addListenner(this, LoginEvent.LOGIN_FAILED);
    }

    @Override
    public void onEvent(Event event)
    {
        Log.Info(this.getClass().getName() + " ---> " + event.type);

        switch (event.type)
        {
            case LoginEvent.LOGIN_SUCCESS:
                this.onLoginSucces();
                break;
            case LoginEvent.LOGIN_FAILED:
                this.onLoginFailed();
                break;
        }
    }

    private void onLoginFailed()
    {
        this.loginController.onLoginFailed();
        this.api.closeAllConnection();
    }

    private void onLoginSucces()
    {
        this.loginController.onLoginSuccess();
        this.api.closeAllConnection();
    }
}
