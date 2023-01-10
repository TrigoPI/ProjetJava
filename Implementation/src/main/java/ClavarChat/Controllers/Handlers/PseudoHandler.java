package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.API.EventAPI;
import ClavarChat.Controllers.API.NetworkAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.ClvcEvent.LoginEvent;
import ClavarChat.Models.ClvcEvent.NewUserEvent;
import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcNetworkMessage.ClvcNetworkMessage;
import ClavarChat.Models.ClvcNetworkMessage.LoginMessage;
import ClavarChat.Utils.Log.Log;

public class PseudoHandler implements MessageListener
{
    private final UserManager userManager;
    private final DataBaseAPI dataBaseAPI;
    private final NetworkAPI networkAPI;
    private final EventAPI eventAPI;

    public PseudoHandler(UserManager userManager, NetworkAPI networkAPI, DataBaseAPI dataBaseAPI, EventAPI eventAPI)
    {
        this.userManager = userManager;
        this.networkAPI = networkAPI;
        this.dataBaseAPI = dataBaseAPI;
        this.eventAPI = eventAPI;
    }

    public boolean checkPseudo()
    {
        int id = this.userManager.getId();
        String pseudo = this.userManager.getPseudo();
        byte[] avatar = this.userManager.getAvatar();

        if (this.userManager.pseudoExist(pseudo))
        {
            Log.Error(PseudoHandler.class.getName() + " Pseudo already used");

            this.userManager.setLogged(false);
            this.userManager.reset();

            return false;
        }

        Log.Info(PseudoHandler.class.getName() + " Pseudo Success");
        this.dataBaseAPI.addUser(id, pseudo, avatar);
        this.userManager.setLogged(true);
        this.networkAPI.sendLogin();
        this.sendUnsentMessages();

        return true;
    }

    @Override
    public void onData(String srcIp, ClvcNetworkMessage message)
    {
        switch (message.type)
        {
            case LoginMessage.PSEUDO -> this.onNewPseudo((LoginMessage)message);
        }
    }

    private void onNewPseudo(LoginMessage message)
    {
        this.eventAPI.notify(new NewUserEvent(message.id, message.pseudo));
    }

    private void sendUnsentMessages()
    {
        for (int conversationId : this.dataBaseAPI.getConversationsId())
        {
            String shared_id = this.dataBaseAPI.getConversationSharedId(conversationId);

            for (int userId : this.dataBaseAPI.getUsersInConversation(conversationId))
            {
                if (this.userManager.isConnected(userId))
                {
                    for (int messageId : this.dataBaseAPI.getUnsentMessage(conversationId))
                    {
                        String message = this.dataBaseAPI.getMessageText(messageId);
                        this.dataBaseAPI.setToSent(messageId);
                        this.networkAPI.sendMessage(userId, shared_id, message);
                    }
                }
            }
        }
    }
}
