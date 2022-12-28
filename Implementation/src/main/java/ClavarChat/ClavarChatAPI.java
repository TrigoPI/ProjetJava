package ClavarChat;

import ClavarChat.Controllers.API.DataBaseAPI.DataBaseAPI;
import ClavarChat.Controllers.ClavarChatRunnable.Login.LoginExecutable;
import ClavarChat.Controllers.API.NetworkAPI.NetworkAPI;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Controllers.Chain.Discover;
import ClavarChat.Controllers.Chain.PseudoVerify;
import ClavarChat.Utils.BytesImage.BytesImage;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.ClavarChatMessage.*;
import ClavarChat.Models.Events.Login.LoginEvent;
import ClavarChat.Models.Events.Login.NewUserEvent;
import ClavarChat.Models.Events.Login.RemoveUserEvent;
import ClavarChat.Models.Events.Message.MessageEvent;
import ClavarChat.Models.Events.Network.NetworkPacketEvent;
import ClavarChat.Models.Message.Message;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;

public class ClavarChatAPI implements Listener
{
    private final EventManager eventManager;
    private final ThreadManager threadManager;
    private final UserManager userManager;

    private final DataBaseAPI dataBaseAPI;
    private final NetworkAPI networkAPI;

    private final Discover discover;

    public ClavarChatAPI(int tcpPort, int udpPort)
    {

        this.eventManager = EventManager.getInstance();
        this.threadManager = new ThreadManager();
        this.userManager = new UserManager();

        this.networkAPI = new NetworkAPI(this.threadManager, this.userManager, tcpPort, udpPort);
        this.dataBaseAPI = new DataBaseAPI(this.userManager);

        this.discover = new Discover(this.networkAPI, this.userManager);
        PseudoVerify pseudoVerify = new PseudoVerify(this.networkAPI, this.userManager);

        this.eventManager.addEvent(MessageEvent.TEXT_MESSAGE);
        this.eventManager.addEvent(LoginEvent.LOGIN_SUCCESS);
        this.eventManager.addEvent(LoginEvent.LOGIN_FAILED);
        this.eventManager.addEvent(NewUserEvent.NEW_USER);
        this.eventManager.addEvent(RemoveUserEvent.REMOVE_USER);
        this.eventManager.addEvent(NetworkPacketEvent.NETWORK_PACKET);

        this.eventManager.addListener(this, NetworkPacketEvent.NETWORK_PACKET);
        this.networkAPI.startServer();
        this.discover.setNext(pseudoVerify);

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
//        this.dataBaseAPI.addUser("user1", 1111, this.userManager.getAvatar(1111));
//        this.dataBaseAPI.addUser("user2", 2222, this.userManager.getAvatar(2222));
//        this.dataBaseAPI.addUser("user3", 3333, this.userManager.getAvatar(3333));
//        this.dataBaseAPI.addUser("user4", 4444, this.userManager.getAvatar(4444));
//
//        this.dataBaseAPI.createConversation("user1", 6969, 1111);
//        this.dataBaseAPI.createConversation("user2", 6969, 2222);
//        this.dataBaseAPI.createConversation("user3", 6969, 3333);
//        this.dataBaseAPI.createConversation("user4", 6969, 4444);
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

        int threadId = this.threadManager.createThread(new LoginExecutable(discover));
        BytesImage avatar = new BytesImage(path);

        this.userManager.setUser(id, pseudo, avatar.getBytes());
        this.dataBaseAPI.addUser(id, pseudo, avatar.getBytes());
        this.threadManager.startThread(threadId);
    }

    public void logout()
    {
        this.networkAPI.sendLogout();
    }

    public void saveMessage(int conversationId, int userId, String text)
    {
        Log.Print(this.getClass().getName() + " Saving message : [" + userId + "] : " + text);
        this.dataBaseAPI.addMessage(conversationId, userId, text);
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

    @Override
    public void onEvent(Event event)
    {
        if (event.type.equals(NetworkPacketEvent.NETWORK_PACKET))
        {
            this.onNetworkPacketEvent((NetworkPacketEvent) event);
        }
    }

    private void onNetworkPacketEvent(NetworkPacketEvent event)
    {
        ClavarChatMessage data = event.data;

        switch (data.type) {
            case LoginMessage.LOGIN -> this.onLogin((LoginMessage) data, event.ip);
            case LoginMessage.LOGOUT -> this.onLogout((LoginMessage) data);
            case DiscoverRequestMessage.DISCOVER_REQUEST -> this.onDiscoverRequest(event.ip);
            case DiscoverResponseMessage.DISCOVER_RESPONSE -> this.onDiscoverResponse((DiscoverResponseMessage) data, event.ip);
            case TextMessage.TEXT_MESSAGE -> this.onTextMessage((TextMessage) data, event.ip);
        }
    }

    private void onLogin(LoginMessage data, String src)
    {
        this.userManager.addUser(data.pseudo, data.id, data.img);
        this.userManager.addIpToUser(data.id, src);
        this.createConversation(data.id, data.pseudo, data.img);
        this.eventManager.notify(new NewUserEvent(data.id, data.pseudo));
    }

    private void onLogout(LoginMessage data)
    {
        this.userManager.removeUser(data.id);
        this.eventManager.notify(new RemoveUserEvent(data.pseudo, data.id));
    }

    private void onDiscoverRequest(String src)
    {
        Log.Print(this.getClass().getName() + " Discover from : " + src);
        this.networkAPI.sendDiscoverResponse(src);
    }

    private void onDiscoverResponse(DiscoverResponseMessage data, String src)
    {
        Log.Info(this.getClass().getName() + " Discover information from user : " + data.pseudo + " / " + "#" + data.id);
        this.userManager.addUser(data.pseudo, data.id, data.avatar);
        this.userManager.addIpToUser(data.id, src);
        this.createConversation(data.id, data.pseudo, data.avatar);
        this.discover.onDiscoverInformation(data, src);
    }

    private void onTextMessage(TextMessage data, String src)
    {
        Log.Info(this.getClass().getName() + " Message from [" + src + "] --> " + data.pseudo + "/#" + data.id + " : " + data.message);
        this.eventManager.notify(new MessageEvent(data.pseudo, data.id, data.message));
    }

    private void createConversation(int userId, String pseudo, byte[] avatar)
    {
        this.dataBaseAPI.addUser(userId, pseudo, avatar);
        if (!this.dataBaseAPI.userExist(userId)) this.dataBaseAPI.createConversation(pseudo, this.getId(), userId);
    }
}