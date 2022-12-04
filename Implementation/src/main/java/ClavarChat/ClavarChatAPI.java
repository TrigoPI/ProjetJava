package ClavarChat;

import ClavarChat.Controllers.NetworkAPI.NetworkAPI;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Controllers.Modules.DiscoverModule;
import ClavarChat.Controllers.Modules.LoginExecutable;
import ClavarChat.Controllers.Modules.LoginVerifyModule;
import ClavarChat.Models.Callback.Callback;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.ClavarChatMessage.*;
import ClavarChat.Models.Events.NetworkPaquetEvent;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Log.Log;

public class ClavarChatAPI implements Listener
{
    private int tcpPort;
    private int udpPort;

    private EventManager eventManager;
    private NetworkManager networkManager;
    private ThreadManager threadManager;
    private UserManager userManager;

    private NetworkAPI networkAPI;

    private DiscoverModule discoverModule;
    private LoginVerifyModule loginVerifyModule;

    public ClavarChatAPI(int tcpPort, int udpPort)
    {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;

        this.eventManager = EventManager.getInstance();
        this.networkManager = new NetworkManager();
        this.threadManager = new ThreadManager();
        this.userManager = new UserManager();

        this.networkAPI = new NetworkAPI(this.threadManager, this.networkManager, this.tcpPort, this.udpPort);

        this.discoverModule = new DiscoverModule(this.networkAPI, this.userManager, this.udpPort);
        this.loginVerifyModule = new LoginVerifyModule(this.networkAPI, this.userManager, this.tcpPort);

        this.discoverModule.setNext(this.loginVerifyModule);

        this.eventManager.addListenner(this, NetworkPaquetEvent.NETWORK_PAQUET);

        this.networkAPI.startServer();
    }

    public void login(String pseudo, String id, Callback callback)
    {
        this.userManager.setUser(pseudo, id);
        this.loginVerifyModule.setCallback(callback);

        int threadId = this.threadManager.createThread(new LoginExecutable(this.discoverModule));
        this.threadManager.startThread(threadId);
    }

    public void sendMessage(String message, String ip)
    {
        if (this.userManager.isLogged())
        {
            UserData user = this.userManager.getUser();
            TextMessage mgs = new TextMessage(user, message);
            this.networkAPI.sendTCP(ip, this.tcpPort, mgs);
        }
        else
        {
            Log.Error(this.getClass().getName() + " Cannot send message, user not logged");
        }
    }

    @Override
    public void onEvent(Event event)
    {
        switch (event.type)
        {
            case NetworkPaquetEvent.NETWORK_PAQUET:
                this.onNetworkPaquetEvent((NetworkPaquetEvent)event);
                break;
        }
    }

    private void onNetworkPaquetEvent(NetworkPaquetEvent event)
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
                this.onDiscoverRequest(data, src);
                break;
            case RESPONSE:
                this.onDiscoverResponse(data, src);
                break;
        }
    }

    private void onLogin(ClavarChatMessage data, String src)
    {
        this.userManager.addUser(data.user, src);
    }

    private void onDiscoverRequest(DiscoverMessage data, String src)
    {
        Log.Print(this.getClass().getName() + " Discover from : " + src);

        if (this.userManager.isLogged())
        {
            int count = this.userManager.getUserCount();
            UserData user = this.userManager.getUser();
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
        this.discoverModule.onDiscoverInformation(data, src);
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