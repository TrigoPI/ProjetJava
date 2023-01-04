package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.NetworkAPI.NetworkAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcMessage.*;
import ClavarChat.Utils.Log.Log;

public class SessionHandler implements MessageListener
{
    private final UserManager userManager;
    private final NetworkAPI networkAPI;

    public SessionHandler(UserManager userManager, NetworkAPI networkAPI)
    {
        this.userManager = userManager;
        this.networkAPI = networkAPI;
    }

    @Override
    public void onData(String dstIp, ClvcMessage message)
    {
        switch (message.type)
        {
            case LoginMessage.LOGIN -> this.onLogin((LoginMessage)message, dstIp);
            case LoginMessage.LOGOUT -> this.onLogout((LoginMessage)message);
        }
    }

    private void onLogin(LoginMessage data, String dstIp)
    {
        this.userManager.addUser(data.pseudo, data.id, data.img);
        this.userManager.addIpToUser(data.id, dstIp);
//        this.createConversation(data.id, data.pseudo, data.img);
//        this.eventManager.notify(new NewUserEvent(data.id, data.pseudo));
    }

    private void onLogout(LoginMessage data)
    {
        this.userManager.removeUser(data.id);
    }
}
