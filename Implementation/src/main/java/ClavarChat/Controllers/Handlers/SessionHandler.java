package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.API.EventAPI;
import ClavarChat.Controllers.API.NetworkAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.ClvcEvent.NewUserEvent;
import ClavarChat.Models.ClvcEvent.RemoveUserEvent;
import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcNetworkMessage.*;
import ClavarChat.Utils.Log.Log;

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
    public void onData(String srcIp, ClvcNetworkMessage message)
    {
        switch (message.type)
        {
            case SharedIDMessage.SHARED_ID -> this.onSharedId((SharedIDMessage)message);
            case SessionMessage.LOGIN -> this.onLogin((SessionMessage)message, srcIp);
            case SessionMessage.LOGOUT -> this.onLogout((SessionMessage)message);
        }
    }

    private void onSharedId(SharedIDMessage data)
    {
        if (!this.dataBaseAPI.conversationExist(data.sharedId)) this.dataBaseAPI.createConversation(data.pseudo, data.sharedId, data.id);
        this.eventAPI.notify(new NewUserEvent(data.id, data.pseudo));
    }

    private void onLogin(SessionMessage data, String dstIp)
    {
        Log.Info(DiscoverHandler.class.getName() + " Login : [ " + dstIp + " ] : " + data.id + " --> " + data.pseudo);

        this.userManager.addUser(data.pseudo, data.id, data.img);
        this.userManager.addIpToUser(data.id, dstIp);

        this.createSharedConversation(data.id, data.pseudo);
        this.sendUnsentMessages(data.id);

        this.dataBaseAPI.addUser(data.id, data.pseudo, data.img);
        this.eventAPI.notify(new NewUserEvent(data.id, data.pseudo));
    }

    private void onLogout(SessionMessage data)
    {
        this.userManager.removeUser(data.id);
        this.eventAPI.notify(new RemoveUserEvent(data.pseudo, data.id));
    }

    private void createSharedConversation(int userId, String pseudo)
    {
        int id = this.dataBaseAPI.createConversation(pseudo, userId);
        String sharedId = this.dataBaseAPI.getConversationSharedId(id);

        this.networkAPI.sendSharedConversationId(userId, sharedId);
    }

    private void sendUnsentMessages(int userId)
    {
        for (int conversationId : this.dataBaseAPI.getConversationWith(userId))
        {
            String shared_id = this.dataBaseAPI.getConversationSharedId(conversationId);

            for (int messageId : this.dataBaseAPI.getUnsentMessage(conversationId))
            {
                String message = this.dataBaseAPI.getMessageText(messageId);
                this.dataBaseAPI.setToSent(messageId);
                this.networkAPI.sendMessage(userId, shared_id, message);
            }
        }
    }
}
