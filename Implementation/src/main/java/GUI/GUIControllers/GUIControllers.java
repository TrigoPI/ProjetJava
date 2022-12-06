package GUI.GUIControllers;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Models.Events.Login.LoginEvent;
import ClavarChat.Models.Events.Login.NewUserEvent;
import ClavarChat.Utils.Log.Log;
import GUI.GUIControllers.Controllers.ClavarChatController;
import GUI.GUIControllers.Controllers.LoginController;
import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Models.Events.Event;

public class GUIControllers implements Listener
{
    private final LoginController loginController;
    private final ClavarChatController clavarChatController;

    private final EventManager eventManager;
    private final ClavarChatAPI api;

    public GUIControllers(ClavarChatAPI api, LoginController loginController, ClavarChatController clavarChatController)
    {
        this.api = api;

        this.loginController = loginController;
        this.clavarChatController = clavarChatController;

        this.eventManager = EventManager.getInstance();

        eventManager.addListenner(this, LoginEvent.LOGIN_SUCCESS);
        eventManager.addListenner(this, LoginEvent.LOGIN_FAILED);
        eventManager.addListenner(this, NewUserEvent.NEW_USER);
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
            case NewUserEvent.NEW_USER:
                NewUserEvent newUserEvent = (NewUserEvent)event;
                this.onNewUser(newUserEvent.pseudo, newUserEvent.id);
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

    private void onNewUser(String pseudo, String id)
    {
        this.clavarChatController.onNewUser(pseudo, id);
    }
}
