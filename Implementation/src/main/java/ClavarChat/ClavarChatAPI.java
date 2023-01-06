package ClavarChat;

import ClavarChat.Controllers.API.DataBaseAPI.DataBaseAPI;
import ClavarChat.Controllers.API.EventAPI.EventAPI;
import ClavarChat.Controllers.Handlers.DiscoverHandler;
import ClavarChat.Controllers.Handlers.PseudoHandler;
import ClavarChat.Controllers.Handlers.SessionHandler;
import ClavarChat.Controllers.API.NetworkAPI.NetworkAPI;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Controllers.Runnables.Discover.Discover;
import ClavarChat.Models.ClvcListener.ClvcListener;
import ClavarChat.Utils.BytesImage.BytesImage;
import ClavarChat.Models.Message.Message;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.Path.Path;

import java.util.ArrayList;

public class ClavarChatAPI
{
    private final UserManager userManager;

    private final EventAPI eventAPI;
    private final DataBaseAPI dataBaseAPI;
    private final NetworkAPI networkAPI;
    private final SessionHandler sessionHandler;
    private final DiscoverHandler discoverHandler;
    private final PseudoHandler pseudoHandler;
    private final ThreadManager threadManager;

    public ClavarChatAPI(int tcpPort, int udpPort)
    {

        this.userManager = new UserManager();
        this.threadManager = new ThreadManager();

        this.networkAPI = new NetworkAPI(this.userManager, tcpPort, udpPort);
        this.dataBaseAPI = new DataBaseAPI(this.userManager);
        this.eventAPI = new EventAPI();

        this.pseudoHandler = new PseudoHandler(this.userManager, this.networkAPI);
        this.sessionHandler = new SessionHandler(this.networkAPI, this.eventAPI, this.dataBaseAPI, this.userManager);
        this.discoverHandler = new DiscoverHandler(this.networkAPI, this.dataBaseAPI, this.userManager, this.pseudoHandler);

        this.networkAPI.addListener(this.sessionHandler);
        this.networkAPI.addListener(this.discoverHandler);
        this.networkAPI.addListener(this.sessionHandler);

        this.networkAPI.startServer();

        this.dataBaseAPI.clear();

//        BytesImage img1 = new BytesImage(Path.getWorkingPath() + "\\src\\main\\resources\\Application\\ClavarChatGUI\\IMG\\Logo.png");
//        BytesImage img2 = new BytesImage(Path.getWorkingPath() + "\\src\\main\\resources\\Application\\ClavarChatGUI\\IMG\\LogoText.png");
//        BytesImage img3 = new BytesImage(Path.getWorkingPath() + "\\src\\main\\resources\\Application\\ClavarChatGUI\\IMG\\user1.jpg");
//        BytesImage img4 = new BytesImage(Path.getWorkingPath() + "\\src\\main\\resources\\Application\\ClavarChatGUI\\IMG\\user2.jpg");
//
//        this.userManager.addUser("user1", 1111, img1.getBytes());
//        this.userManager.addUser("user2", 2222, img2.getBytes());
//        this.userManager.addUser("user3", 3333, img3.getBytes());
//        this.userManager.addUser("user4", 4444, img4.getBytes());
//
//        this.userManager.addIpToUser(1111, "192.168.1.11");
//        this.userManager.addIpToUser(2222, "192.168.1.22");
//        this.userManager.addIpToUser(3333, "192.168.1.33");
//        this.userManager.addIpToUser(4444, "192.168.1.44");
//
//        this.dataBaseAPI.addUser(1111, "user1", this.userManager.getAvatar(1111));
//        this.dataBaseAPI.addUser(2222, "user2", this.userManager.getAvatar(2222));
//        this.dataBaseAPI.addUser(3333, "user3", this.userManager.getAvatar(3333));
//        this.dataBaseAPI.addUser(4444, "user4", this.userManager.getAvatar(4444));
//
//        this.dataBaseAPI.createConversation("user1", 7070, 1111);
//        this.dataBaseAPI.createConversation("user2", 7070, 2222);
//        this.dataBaseAPI.createConversation("user3", 7070, 3333);
//        this.dataBaseAPI.createConversation("user4", 7070, 4444);
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

    public void login(int id, String pseudo, String path)
    {
        Log.Print(this.getClass().getName() + " Trying to login with : " + pseudo + "/#" + id);

        Discover discover = new Discover(this.discoverHandler, this.eventAPI);
        int threadId = this.threadManager.createThread(discover);
        BytesImage avatar = new BytesImage(path);

        this.userManager.setUser(id, pseudo, avatar.getBytes());
        this.dataBaseAPI.addUser(id, pseudo, avatar.getBytes());
        this.threadManager.startThread(threadId);
    }

    public void logout()
    {
        this.networkAPI.sendLogout();
    }

    public void sendMessage(int srcId, int dstId, int conversationId, String message)
    {
        Log.Print(this.getClass().getName() + " Saving message : [" + srcId + "] " + srcId + " --> " + dstId + " : " + message);
        this.dataBaseAPI.addMessage(conversationId, srcId, message);
        this.networkAPI.sendMessage(dstId, message);
    }

    public void closeServers()
    {
        this.networkAPI.closeServer();
    }

    public void closeAllClients()
    {
        this.networkAPI.closeAllClients();
    }

//    private void onTextMessage(TextMessage data, String src)
//    {
//        Log.Info(this.getClass().getName() + " Message from [" + src + "] --> " + data.pseudo + "/#" + data.id + " : " + data.message);
//        this.eventManager.notify(new MessageEvent(data.pseudo, data.id, data.message));
//    }
//

    public void addListener(ClvcListener listener, String eventName)
    {
        this.eventAPI.addListener(listener, eventName);
    }
}