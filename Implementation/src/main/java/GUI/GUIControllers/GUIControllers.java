package GUI.GUIControllers;

import ClavarChat.ClavarChatAPI;
import GUI.GUIControllers.Controllers.LoginController;
import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Models.Events.Event;

public class GUIControllers implements Listener
{
    private LoginController loginController;

    public GUIControllers(LoginController loginController)
    {
        this.loginController = loginController;
    }

    @Override
    public void onEvent(Event event)
    {

    }

    private void onLoginResponse()
    {
        this.loginController.onLoginResponse();
    }
}
