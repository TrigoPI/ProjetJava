package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.API.NetworkAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Utils.Log.Log;

public class PseudoHandler
{
    private final UserManager userManager;
    private final DataBaseAPI dataBaseAPI;
    private final NetworkAPI networkAPI;

    public PseudoHandler(UserManager userManager, NetworkAPI networkAPI, DataBaseAPI dataBaseAPI)
    {
        this.userManager = userManager;
        this.networkAPI = networkAPI;
        this.dataBaseAPI = dataBaseAPI;
    }

    public void changePseudo(String newPseudo)
    {

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
