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
import ClavarChat.Models.Events.Network.NetworkPacketEvent;
import ClavarChat.Models.Users.User;
import ClavarChat.Utils.Log.Log;

public class ClavarChatAPI implements Listener
{
    private final int tcpPort;
    private final int udpPort;

    private final EventManager eventManager;
    private final ThreadManager threadManager;
    private final UserManager userManager;

    private final NetworkAPI networkAPI;

    private final Discover discover;
    private final PseudoVerify pseudoVerify;

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
        this.eventManager.addEvent(NetworkPacketEvent.NETWORK_PACKET);
        this.eventManager.addListenner(this, NetworkPacketEvent.NETWORK_PACKET);

        this.networkAPI.startServer();
    }

    public String getPseudo()
    {
        return this.userManager.getUser().pseudo;
    }

    public String getId()
    {
        return this.userManager.getUser().id;
    }

    public void login(String pseudo, String id)
    {
        Log.Print(this.getClass().getName() + " Trying to login with : " + pseudo + "/#" + id);

        int threadId = this.threadManager.createThread(new LoginExecutable(this.discover, new LoginRequest(pseudo, id, "")));
        this.threadManager.startThread(threadId);
    }

    public void sendMessage(String message, String ip)
    {
        if (this.userManager.isLogged())
        {
            User user = this.userManager.getUser();
            TextMessage mgs = new TextMessage(user, message);
            this.networkAPI.sendTCP(ip, this.tcpPort, mgs);
        }
        else
        {
            Log.Error(this.getClass().getName() + " Cannot send message, user not logged");
        }
    }

    public void closeAllConnection()
    {
        this.networkAPI.closeAll();
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
            case LOGIN:
                this.onLogin(data, event.ip);
                break;
            case LOGOUT:
                break;
            case DISCOVER:
                this.onDiscover((DiscoverMessage)data, event.ip);
                break;
            case DATA:
                this.onData((DataMessage)data, event.ip);
                break;
        }
    }

    private void onDiscover(DiscoverMessage data, String src)
    {
        switch (data.discoverType)
        {
            case REQUEST:
                this.onDiscoverRequest(src);
                break;
            case RESPONSE:
                this.onDiscoverResponse(data, src);
                break;
        }
    }

    private void onLogin(ClavarChatMessage data, String src)
    {
        this.userManager.addUser(data.user, src);
        this.eventManager.notiy(new NewUserEvent(data.user.pseudo, data.user.id));
    }

    private void onDiscoverRequest(String src)
    {
        Log.Print(this.getClass().getName() + " Discover from : " + src);

        if (this.userManager.isLogged())
        {
            int count = this.userManager.getUserCount();
            User user = this.userManager.getUser();
            DiscoverMessage informationMessage = new DiscoverMessage(user, count);
            this.networkAPI.sendTCP(src, this.tcpPort, informationMessage);
        }
        else
        {
            Log.Error(this.getClass().getName() + " User not logged cannot respond to DISCOVER");
        }
    }

    private void onDiscoverResponse(DiscoverMessage data, String src)
    {
        Log.Info(this.getClass().getName() + " Discover information from user : " + data.user.pseudo + " / " + "#" + data.user.id);
        this.discover.onDiscoverInformation(data, src);
    }

    private void onData(DataMessage data, String src)
    {
        switch (data.dataType)
        {
            case TEXT:
                this.onTextMessage((TextMessage)data, src);
                break;
            case FILE:
                break;
        }
    }

    private void onTextMessage(TextMessage data, String src)
    {
        Log.Info(this.getClass().getName() + " Message from " + src + " : " + data.message);
    }
}