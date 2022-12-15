package ClavarChat;

import ClavarChat.Controllers.Managers.Conversation.ConversationManager;
import ClavarChat.Controllers.ThreadExecutable.Login.LoginExecutable;
import ClavarChat.Controllers.API.NetworkAPI.NetworkAPI;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Controllers.Chain.Discover;
import ClavarChat.Controllers.Chain.PseudoVerify;
import ClavarChat.Models.ByteImage.ByteImage;
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
import ClavarChat.Utils.Path.Path;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class ClavarChatAPI implements Listener
{
    private final int tcpPort;
    private final int udpPort;

    private final EventManager eventManager;
    private final ThreadManager threadManager;
    private final UserManager userManager;
    private final ConversationManager conversationManager;

    private final NetworkAPI networkAPI;

    private Discover discover;
    private PseudoVerify pseudoVerify;

    public ClavarChatAPI(int tcpPort, int udpPort)
    {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;

        this.eventManager = EventManager.getInstance();
        this.threadManager = new ThreadManager();
        this.userManager = new UserManager();
        this.conversationManager = new ConversationManager();

        this.networkAPI = new NetworkAPI(this.threadManager, this.userManager, this.tcpPort, this.udpPort);

        this.discover = new Discover(this.networkAPI, this.userManager, this.udpPort);
        this.pseudoVerify = new PseudoVerify(this.networkAPI, this.userManager, this.tcpPort);

        this.discover.setNext(this.pseudoVerify);

        this.eventManager.addEvent(MessageEvent.TEXT_MESSAGE);
        this.eventManager.addEvent(LoginEvent.LOGIN_SUCCESS);
        this.eventManager.addEvent(LoginEvent.LOGIN_FAILED);
        this.eventManager.addEvent(NewUserEvent.NEW_USER);
        this.eventManager.addEvent(RemoveUserEvent.REMOVE_USER);
        this.eventManager.addEvent(NetworkPacketEvent.NETWORK_PACKET);

        this.eventManager.addListenner(this, NetworkPacketEvent.NETWORK_PACKET);

        this.userManager.addUser(new User("user1", "9999"), "192.168.1.100");
        this.userManager.addUser(new User("user2", "4444"), "192.168.1.100");

        this.userManager.setAvatar("user1", new Image(Path.getWorkingPath() + "\\src\\main\\resources\\Application\\ClavarChatGUI\\IMG\\user2.jpg"));
        this.userManager.setAvatar("user2", new Image(Path.getWorkingPath() + "\\src\\main\\resources\\Application\\ClavarChatGUI\\IMG\\avatar.jpg"));

        this.networkAPI.startServer();
    }

    public String getPseudo()
    {
        User user = this.userManager.getUser();
        return (user == null)?null:user.pseudo;
    }

    public String getId()
    {
        User user = this.userManager.getUser();
        return (user == null)?null:user.id;
    }

    public String getId(String pseudo)
    {
        User user = this.userManager.getUser(pseudo);
        return (user == null)?null:user.id;
    }

    public ArrayList<Message> getConversation(String pseudo)
    {
        return this.conversationManager.getConversation(pseudo);
    }

    public ArrayList<User> getUsers()
    {
        return this.userManager.getUsers();
    }

    public Image getAvatar()
    {
        return this.userManager.getAvatar();
    }

    public Image getAvatar(String pseudo)
    {
        return this.userManager.getAvatar(pseudo);
    }

    public boolean conversationExist(String conversationName)
    {
        return this.conversationManager.conversationExist(conversationName);
    }

    public void setAvatar(Image image)
    {
        this.userManager.setAvatar(image);
    }

    public void login(String pseudo, String id, String path)
    {
        Log.Print(this.getClass().getName() + " Trying to login with : " + pseudo + "/#" + id);

        int threadId = this.threadManager.createThread(new LoginExecutable(discover));

        this.userManager.setUser(pseudo, id);
        this.userManager.setAvatar(new Image("file:" + path));
        this.threadManager.startThread(threadId);
    }

    public void logout()
    {
        this.networkAPI.sendLogout();
    }

    public void createConversation(String conversationName)
    {
        this.conversationManager.createConversation(conversationName);
    }

    public void saveMessage(String conversationName, String src, String dst, String text)
    {
        Log.Print(this.getClass().getName() + " Saving message : [" + src + "] --> [" + dst + "] : " + text);
        this.conversationManager.addMessage(conversationName, src, dst, text);
    }

    public void sendMessage(String pseudo, String message)
    {
        this.networkAPI.sendMessage(pseudo, message);
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
        switch (event.type)
        {
            case NetworkPacketEvent.NETWORK_PACKET:
                this.onNetworkPacketEvent((NetworkPacketEvent)event);
                break;
        }
    }

    private void onNetworkPacketEvent(NetworkPacketEvent event)
    {
        ClavarChatMessage data = event.data;

        switch (data.type)
        {
            case LoginMessage.LOGIN:
                this.onLogin((LoginMessage)data, event.ip);
                break;
            case LoginMessage.LOGOUT:
                this.onLogout((LoginMessage)data);
                break;
            case DiscoverRequestMessage.DISCOVER_REQUEST:
                this.onDiscoverRequest(event.ip);
                break;
            case DiscoverResponseMessage.DISCOVER_RESPONSE:
                this.onDiscoverResponse((DiscoverResponseMessage)data, event.ip);
                break;
            case TextMessage.TEXT_MESSAGE:
                this.onTextMessage((TextMessage)data, event.ip);
                break;
        }
    }

    private void onLogin(LoginMessage data, String src)
    {
        Image avatar = ByteImage.decode(data.img);

        this.userManager.addUser(new User(data.pseudo, data.id), src);
        this.userManager.setAvatar(data.pseudo, avatar);
        this.eventManager.notiy(new NewUserEvent(data.pseudo));
    }

    private void onLogout(LoginMessage data)
    {
        this.userManager.removeUser(data.pseudo);
        this.eventManager.notiy(new RemoveUserEvent(data.pseudo));
    }

    private void onDiscoverRequest(String src)
    {
        Log.Print(this.getClass().getName() + " Discover from : " + src);
        this.networkAPI.sendDiscoverResponse(src);
    }

    private void onDiscoverResponse(DiscoverResponseMessage data, String src)
    {
        Log.Info(this.getClass().getName() + " Discover information from user : " + data.pseudo + " / " + "#" + data.id);

        Image avatar = ByteImage.decode(data.avatar);

        this.userManager.addUser(new User(data.pseudo, data.id), src);
        this.userManager.setAvatar(data.pseudo, avatar);
        this.discover.onDiscoverInformation(data, src);
    }

    private void onTextMessage(TextMessage data, String src)
    {
        Log.Info(this.getClass().getName() + " Message from [" + src + "] --> " + data.pseudo + "/#" + data.id + " : " + data.message);
        this.eventManager.notiy(new MessageEvent(data.pseudo, data.id, data.message));
    }
}