package GUI.GUIControllers;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Models.ClvcEvent.ClvcEvent;
import ClavarChat.Models.ClvcEvent.LoginEvent;
import ClavarChat.Models.ClvcEvent.NewUserEvent;
import ClavarChat.Models.ClvcEvent.RemoveUserEvent;
import ClavarChat.Models.ClvcEvent.MessageEvent;
import ClavarChat.Models.ClvcListener.ClvcListener;
import ClavarChat.Utils.Log.Log;
import GUI.GUIControllers.Controllers.ClavarChatController;
import GUI.GUIControllers.Controllers.LoginController;

public class GUIControllers implements ClvcListener
{
    private final LoginController loginController;
    private final ClavarChatController clavarChatController;

    private final ClavarChatAPI api;

    public GUIControllers(ClavarChatAPI api, LoginController loginController, ClavarChatController clavarChatController)
    {
        this.api = api;

        this.loginController = loginController;
        this.clavarChatController = clavarChatController;

        this.api.addListener(this, MessageEvent.MESSAGE_EVENT);
        this.api.addListener(this, RemoveUserEvent.REMOVE_USER);
        this.api.addListener(this, LoginEvent.LOGIN_SUCCESS);
        this.api.addListener(this, LoginEvent.LOGIN_FAILED);
        this.api.addListener(this, NewUserEvent.NEW_USER);
    }

    @Override
    public void onEvent(ClvcEvent event)
    {
        Log.Info(this.getClass().getName() + " ---> " + event.type);

        switch (event.type)
        {
            case LoginEvent.LOGIN_SUCCESS:
                this.onLoginSuccess();
                break;
            case LoginEvent.LOGIN_FAILED:
                this.onLoginFailed();
                break;
            case NewUserEvent.NEW_USER:
                NewUserEvent newUserEvent = (NewUserEvent)event;
                this.onNewUser(newUserEvent.userId);
                break;
            case RemoveUserEvent.REMOVE_USER:
                RemoveUserEvent removeUserEvent = (RemoveUserEvent)event;
                this.onRemoveUser(removeUserEvent.id);
                break;
            case MessageEvent.MESSAGE_EVENT:
                this.onTextMessage();
                break;
        }
    }

    private void onLoginFailed()
    {
        this.loginController.onLoginFailed();
    }

    private void onLoginSuccess()
    {
        this.loginController.onLoginSuccess();
    }

    private void onTextMessage()
    {
        this.clavarChatController.onTextMessage();
    }

    private void onRemoveUser(int userId)
    {
        this.clavarChatController.onRemoveUser(userId);
    }

    private void onNewUser(int userId)
    {
        this.clavarChatController.onNewUser(userId);
    }
}
