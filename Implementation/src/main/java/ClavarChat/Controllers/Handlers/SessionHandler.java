package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.DataBaseAPI.DataBaseAPI;
import ClavarChat.Controllers.API.EventAPI.EventAPI;
import ClavarChat.Controllers.API.NetworkAPI.NetworkAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.ClvcEvent.Login.NewUserEvent;
import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcMessage.*;

public class SessionHandler implements MessageListener
{
    private final NetworkAPI networkAPI;
    private final EventAPI eventAPI;
    private final DataBaseAPI dataBaseAPI;
    private final UserManager userManager;

    public SessionHandler(NetworkAPI networkAPI, EventAPI eventAPI, DataBaseAPI dataBaseAPI, UserManager userManager)
    {
        this.networkAPI = networkAPI;
        this.eventAPI = eventAPI;
        this.dataBaseAPI = dataBaseAPI;
        this.userManager = userManager;
    }

    @Override
    public void onData(String srcIp, ClvcMessage message)
    {
        switch (message.type)
        {
            case LoginMessage.LOGIN -> this.onLogin((LoginMessage)message, srcIp);
            case LoginMessage.LOGOUT -> this.onLogout((LoginMessage)message);
        }
    }

    private void onLogin(LoginMessage data, String dstIp)
    {
        this.userManager.addUser(data.pseudo, data.id, data.img);
        this.dataBaseAPI.addUser(data.id, data.pseudo, data.img);
        this.userManager.addIpToUser(data.id, dstIp);
//        this.dataBaseAPI.createConversation(data.pseudo, data.id);
        this.eventAPI.notify(new NewUserEvent(data.id, data.pseudo));
    }

    private void onLogout(LoginMessage data)
    {
        this.userManager.removeUser(data.id);
    }
}
