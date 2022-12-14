package ClavarChat;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.API.EventAPI;
import ClavarChat.Controllers.Handlers.DiscoverHandler;
import ClavarChat.Controllers.Handlers.MessageHandler;
import ClavarChat.Controllers.Handlers.PseudoHandler;
import ClavarChat.Controllers.Handlers.SessionHandler;
import ClavarChat.Controllers.API.NetworkAPI;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Controllers.Runnables.Discover.Discover;
import ClavarChat.Models.ClvcListener.ClvcListener;
import ClavarChat.Models.ClvcNetworkMessage.TextMessage;
import ClavarChat.Utils.BytesImage.BytesImage;
import ClavarChat.Models.Message.Message;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;
import Resources.Resources;

import java.util.ArrayList;

public class ClavarChatAPI
{
    private final UserManager userManager;

    private final EventAPI eventAPI;
    private final DataBaseAPI dataBaseAPI;
    private final NetworkAPI networkAPI;
    private final DiscoverHandler discoverHandler;
    private final MessageHandler messageHandler;
    private final PseudoHandler pseudoHandler;

    private final ThreadManager threadManager;

    public ClavarChatAPI(int tcpPort, int udpPort)
    {

        this.userManager = new UserManager();
        this.threadManager = new ThreadManager();

        this.networkAPI = new NetworkAPI(this.userManager, tcpPort, udpPort);
        this.dataBaseAPI = new DataBaseAPI(this.userManager);
        this.eventAPI = new EventAPI();

        SessionHandler sessionHandler = new SessionHandler(this.networkAPI, this.eventAPI, this.dataBaseAPI, this.userManager);
        this.pseudoHandler = new PseudoHandler(this.userManager, this.networkAPI, this.dataBaseAPI, this.eventAPI);
        this.messageHandler = new MessageHandler(this.eventAPI, this.dataBaseAPI, this.userManager);
        this.discoverHandler = new DiscoverHandler(this.networkAPI, this.dataBaseAPI, this.userManager, pseudoHandler);

        this.networkAPI.addListener(this.pseudoHandler);
        this.networkAPI.addListener(this.discoverHandler);
        this.networkAPI.addListener(this.messageHandler);
        this.networkAPI.addListener(sessionHandler);

        this.networkAPI.startServer();
    }

    public boolean login(String pseudo, String password)
    {
        Log.Print(this.getClass().getName() + " Trying to login with : " + pseudo);

        BytesImage avatar;
        Discover discover = new Discover(this.discoverHandler, this.eventAPI);

        int userId = this.userManager.getId();
        int threadId = this.threadManager.createThread(discover);

        if (!this.userManager.getPasswordHash().equals(password)) return false;

        if (this.dataBaseAPI.userExist(userId))
        {
            byte[] buffer = this.dataBaseAPI.getUserAvatar(userId);
            avatar = new BytesImage(buffer);
        }
        else
        {
            avatar = new BytesImage(Resources.IMG.USER_1);
        }

        this.userManager.setUser(pseudo, avatar.getBytes());
        this.threadManager.startThread(threadId);

        return true;
    }

    public boolean hasMessages()
    {
        return this.messageHandler.hasMessage();
    }

    public boolean isConnected(int userId)
    {
        return this.userManager.isConnected(userId);
    }

    public int getId()
    {
        return this.userManager.getId();
    }

    public int getId(String pseudo)
    {
        return this.userManager.getId(pseudo);
    }

    public TextMessage getLastMessage()
    {
        return this.messageHandler.getLastMessage();
    }

    public ArrayList<User> getUsers()
    {
        return this.userManager.getUsers();
    }

    public ArrayList<Integer> getConversationIdWith(int userId)
    {
        return this.dataBaseAPI.getConversationWith(userId);
    }

    public ArrayList<Integer> getConversationsIdInDataBase()
    {
        return this.dataBaseAPI.getConversationsId();
    }

    public ArrayList<Integer> getUsersIdInDataBase()
    {
        return this.dataBaseAPI.getUsersId();
    }

    public ArrayList<Integer> getUserIdInConversation(int conversationId)
    {
        return this.dataBaseAPI.getUsersInConversation(conversationId);
    }

    public ArrayList<Integer> getMessagesIdInDataBase(int conversationId)
    {
        return this.dataBaseAPI.getMessagesId(conversationId);
    }

    public String getPseudoFromDataBase(int userId)
    {
        return this.dataBaseAPI.getUserPseudo(userId);
    }

    public String getPseudo()
    {
        return this.userManager.getPseudo();
    }

    public String getPseudo(int id)
    {
        return this.userManager.getPseudo(id);
    }

    public String getConversationSharedId(int conversationId)
    {
        return this.dataBaseAPI.getConversationSharedId(conversationId);
    }

    public User getUserInDataBase(int userId)
    {
        String pseudo = this.dataBaseAPI.getUserPseudo(userId);
        return new User(pseudo, userId);
    }

    public User getUser()
    {
        return this.userManager.getUser();
    }

    public User getUser(int userId)
    {
        return this.userManager.getUser(userId);
    }

    public Message getMessageInDataBase(int messageId)
    {
        int userId  = this.dataBaseAPI.getMessageUserId(messageId);
        String text = this.dataBaseAPI.getMessageText(messageId);
        return new Message(userId, text);
    }

    public Message getLastMessage(int conversationId)
    {
        int messageId = this.dataBaseAPI.getLastMessageId(conversationId);
        if (messageId == -1) return null;
        int userId = this.dataBaseAPI.getMessageUserId(messageId);
        String text = this.dataBaseAPI.getMessageText(messageId);
        return new Message(userId, text);
    }

    public BytesImage getAvatar()
    {
        return new BytesImage(this.userManager.getAvatar());
    }

    public BytesImage getAvatarFromDataBase(int userId)
    {
        return  new BytesImage(this.dataBaseAPI.getUserAvatar(userId));
    }

    public BytesImage getAvatar(int userId)
    {
        return new BytesImage(this.userManager.getAvatar(userId));
    }

    public void logout()
    {
        this.networkAPI.sendLogout();
    }

    public void sendNewPseudo()
    {
        this.networkAPI.sendNewPseudo();
    }

    public void sendMessage(int srcId, int dstId, int conversationId, String message)
    {
        Log.Print(this.getClass().getName() + " Saving message : [" + srcId + "] " + srcId + " --> " + dstId + " : " + message);
        String sharedId = this.getConversationSharedId(conversationId);
        this.dataBaseAPI.addMessage(conversationId, srcId, dstId, message);
        this.networkAPI.sendMessage(dstId, sharedId, message);
    }

    public void updatePseudo(int userId,String pseudo)
    {
        this.userManager.changePseudo(userId, pseudo);
        this.dataBaseAPI.updatePseudo(userId, pseudo);
    }

    public void updateAvatar(int userId, byte[] avatar)
    {
        this.userManager.changeAvatar(userId, avatar);
        this.dataBaseAPI.updateAvatar(userId, avatar);
    }

    public void addListener(ClvcListener listener, String eventName)
    {
        this.eventAPI.addListener(listener, eventName);
    }

    public void stop()
    {
        this.logout();
        this.networkAPI.closeAllClients();
        this.networkAPI.closeServer();
        this.networkAPI.closeSocketObserver();
    }
}