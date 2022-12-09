package ClavarChat;

import ClavarChat.Controllers.NetworkAPI.NetworkAPI;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Controllers.Chain.Discover;
import ClavarChat.Controllers.Chain.PseudoVerify;
import ClavarChat.Controllers.ThreadExecutable.Login.LoginExecutable;
import ClavarChat.Models.ChainData.Request.LoginRequest;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.ClavarChatMessage.*;
import ClavarChat.Models.Events.Login.LoginEvent;
import ClavarChat.Models.Events.Login.NewUserEvent;
import ClavarChat.Models.Events.Login.RemoveUserEvent;
import ClavarChat.Models.Events.Network.NetworkPacketEvent;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class ClavarChatAPI implements Listener
{
    private final int tcpPort;
    private final int udpPort;

    private final EventManager eventManager;
    private final ThreadManager threadManager;
    private final UserManager userManager;

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

        this.networkAPI = new NetworkAPI(this.threadManager, this.tcpPort, this.udpPort);

        this.discover = new Discover(this.networkAPI, this.userManager, this.udpPort);
        this.pseudoVerify = new PseudoVerify(this.networkAPI, this.userManager, this.tcpPort);

        this.discover.setNext(this.pseudoVerify);

        this.eventManager.addEvent(LoginEvent.LOGIN_SUCCESS);
        this.eventManager.addEvent(LoginEvent.LOGIN_FAILED);
        this.eventManager.addEvent(NewUserEvent.NEW_USER);
        this.eventManager.addEvent(RemoveUserEvent.REMOVE_USER);
        this.eventManager.addEvent(NetworkPacketEvent.NETWORK_PACKET);
        this.eventManager.addListenner(this, NetworkPacketEvent.NETWORK_PACKET);

        this.networkAPI.startServer();

//        this.userManager.addUser(new User("user1", "1111"), "192.168.1.3");
//        this.userManager.addUser(new User("user2", "2222"), "192.168.1.4");
//        this.userManager.addUser(new User("user3", "3333"), "192.168.1.5");
//        this.userManager.addUser(new User("user4", "4444"), "192.168.1.6");
//        this.userManager.addUser(new User("user5", "5555"), "192.168.1.7");
//        this.userManager.addUser(new User("user6", "6666"), "192.168.1.8");
//        this.userManager.addUser(new User("user7", "7777"), "192.168.1.9");
//
//        Image img1 = new Image("C:\\Users\\payet\\Desktop\\programs\\Java\\ProjetJava\\Implementation\\src\\main\\resources\\Application\\ClavarChatGUI\\IMG\\user1.jpg");
//        Image img2 = new Image("C:\\Users\\payet\\Desktop\\programs\\Java\\ProjetJava\\Implementation\\src\\main\\resources\\Application\\ClavarChatGUI\\IMG\\user2.jpg");
//
//        this.userManager.setAvatar("user1", img1);
//        this.userManager.setAvatar("user2", img2);
//        this.userManager.setAvatar("user3", img1);
//        this.userManager.setAvatar("user4", img1);
//        this.userManager.setAvatar("user5", img1);
//        this.userManager.setAvatar("user6", img1);
//        this.userManager.setAvatar("user7", img1);
//
//        this.userManager.setUser("Alexis", "0000");
//        this.userManager.setLogged(true);
    }


    public String getPseudo()
    {
        return this.userManager.getUser().pseudo;
    }

    public String getId()
    {
        return this.userManager.getUser().id;
    }

    public String getId(String pseudo)
    {
        User user = this.userManager.getUser(pseudo);
        return (user == null)?null:user.id;
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

    public void setAvatar(String path)
    {
        this.userManager.setAvatar(path);
    }

    public void login(String pseudo, String id)
    {
        Log.Print(this.getClass().getName() + " Trying to login with : " + pseudo + "/#" + id);
        int threadId = this.threadManager.createThread(new LoginExecutable(discover, new LoginRequest(pseudo, id, "")));
        this.threadManager.startThread(threadId);
    }

    public void logout()
    {
        User me = this.userManager.getUser();

        for (String ip : this.networkAPI.getBroadcastAddresses())
        {
            LoginMessage message = new LoginMessage(LoginMessage.LOGOUT, me.pseudo, me.id);
            this.networkAPI.sendUDP(ip, this.udpPort, message);
        }
    }

    public void sendMessage(String pseudo, String message)
    {
        if (this.userManager.isLogged())
        {
            User user = this.userManager.getUser();
            String ip = this.userManager.getUserIP(pseudo).get(0);

            TextMessage mgs = new TextMessage(user.pseudo, user.id, message);
            this.networkAPI.sendTCP(ip, this.tcpPort, mgs);
        }
        else
        {
            Log.Error(this.getClass().getName() + " Cannot send message, user not logged");
        }
    }

    public void closeServers()
    {
        this.networkAPI.closeServer();
    }

    public void closeAllClient()
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
            case TextMessage.TEXT_MESSAGE:
                this.onTextMessage((TextMessage)data, event.ip);
                break;
        }
    }

    private void onLogin(LoginMessage data, String src)
    {
        this.userManager.addUser(new User(data.pseudo, data.id), src);
        this.eventManager.notiy(new NewUserEvent(data.pseudo, data.id));
    }

    private void onLogout(LoginMessage data)
    {
        this.userManager.removeUser(data.pseudo);
        this.eventManager.notiy(new RemoveUserEvent(data.pseudo));
    }

    private void onDiscoverRequest(String src)
    {
        Log.Print(this.getClass().getName() + " Discover from : " + src);

        if (this.userManager.isLogged())
        {
            int count = this.userManager.getUserCount();
            User user = this.userManager.getUser();
            DiscoverResponseMessage informationMessage = new DiscoverResponseMessage(user.pseudo, user.id, count);
            this.networkAPI.sendTCP(src, this.tcpPort, informationMessage);
        }
        else
        {
            Log.Error(this.getClass().getName() + " User not logged cannot respond to DISCOVER");
        }
    }

    private void onDiscoverResponse(DiscoverResponseMessage data, String src)
    {
        Log.Info(this.getClass().getName() + " Discover information from user : " + data.pseudo + " / " + "#" + data.id);
        this.userManager.addUser(new User(data.pseudo, data.id), src);
        this.userManager.setAvatar(data.pseudo, new Image("C:\\Users\\payet\\Desktop\\programs\\Java\\ProjetJava\\Implementation\\src\\main\\resources\\Application\\ClavarChatGUI\\IMG\\user1.jpg"));
        this.discover.onDiscoverInformation(data, src);
    }

    private void onTextMessage(TextMessage data, String src)
    {
        Log.Info(this.getClass().getName() + " Message from [" + src + "] --> " + data.pseudo + "/#" + data.id + " : " + data.message);
    }
}