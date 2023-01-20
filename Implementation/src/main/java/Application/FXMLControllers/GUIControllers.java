package Application.FXMLControllers;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Models.ClvcEvent.*;
import ClavarChat.Models.ClvcListener.ClvcListener;
import ClavarChat.Utils.Log.Log;
import Application.FXMLControllers.Controllers.ClavarChatController;
import Application.FXMLControllers.Controllers.LoginController;

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
        this.api.addListener(this, TypingEvent.TYPING);
    }

    @Override
    public void onEvent(ClvcEvent event)
    {
        Log.Info(this.getClass().getName() + " ---> " + event.type);

        switch (event.type) {
            case NewUserEvent.NEW_USER :
                this.onNewUser((NewUserEvent)event);
                break;
            case RemoveUserEvent.REMOVE_USER :
                this.onRemoveUser((RemoveUserEvent)event);
                break;
            case TypingEvent.TYPING :
                this.onTyping((TypingEvent)event);
                break;
            case LoginEvent.LOGIN_SUCCESS :
                this.onLoginSuccess();
                break;
            case LoginEvent.LOGIN_FAILED :
                this.onLoginFailed();
                break;
            case MessageEvent.MESSAGE_EVENT :
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

    private void onRemoveUser(RemoveUserEvent event)
    {
        this.clavarChatController.onRemoveUser(event.id);
    }

    private void onNewUser(NewUserEvent event)
    {
        this.clavarChatController.onNewUser(event.userId);
    }

    private void onTyping(TypingEvent event)
    {
        this.clavarChatController.onTyping(event.userId, event.sharedId, event.isTyping);
    }
}
