package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.API.EventAPI;
import ClavarChat.Controllers.API.NetworkAPI;
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
            case SharedIdMessage.SHARED_ID -> this.onSharedId((SharedIdMessage)message);
            case LoginMessage.LOGIN -> this.onLogin((LoginMessage)message, srcIp);
            case LoginMessage.LOGOUT -> this.onLogout((LoginMessage)message);
        }
    }

    private void onSharedId(SharedIdMessage data)
    {
        this.dataBaseAPI.createConversation(data.pseudo, data.sharedId, data.id);
        this.eventAPI.notify(new NewUserEvent(data.id, data.pseudo));
    }

    private void onLogin(LoginMessage data, String dstIp)
    {

        this.userManager.addUser(data.pseudo, data.id, data.img);
        this.userManager.addIpToUser(data.id, dstIp);

        this.createSharedConversation(data.id, data.pseudo, data.img);

        this.networkAPI.closeClient(data.id);

        this.eventAPI.notify(new NewUserEvent(data.id, data.pseudo));
    }

    private void onLogout(LoginMessage data)
    {
        this.userManager.removeUser(data.id);
    }

    private void createSharedConversation(int userId, String pseudo, byte[] img)
    {
        if (this.dataBaseAPI.userExist(userId)) return;

        this.dataBaseAPI.addUser(userId, pseudo, img);
        int id = this.dataBaseAPI.createConversation(pseudo, userId);
        String sharedId = this.dataBaseAPI.getConversationSharedId(id);
        this.networkAPI.sendSharedConversationId(userId, sharedId);

    }
}
