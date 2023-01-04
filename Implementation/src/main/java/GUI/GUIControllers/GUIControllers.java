package GUI.GUIControllers;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Models.ClvcEvent.ClvcEvent;
import ClavarChat.Models.ClvcEvent.Login.LoginEvent;
import ClavarChat.Models.ClvcEvent.Login.NewUserEvent;
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

        api.addListener(this, LoginEvent.LOGIN_SUCCESS);
        api.addListener(this, LoginEvent.LOGIN_FAILED);
        api.addListener(this, NewUserEvent.NEW_USER);
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
//            case RemoveUserEvent.REMOVE_USER:
//                RemoveUserEvent removeUserEvent = (RemoveUserEvent)event;
//                this.onRemoveUser(removeUserEvent.id);
//                break;
//            case MessageEvent.TEXT_MESSAGE:
//                MessageEvent messageEvent = (MessageEvent)event;
//                this.onTextMessage(messageEvent.pseudo, messageEvent.message);
//                break;
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

    private void onTextMessage(String pseudo, String message)
    {
//        this.clavarChatController.onTextMessage(pseudo, message);
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
