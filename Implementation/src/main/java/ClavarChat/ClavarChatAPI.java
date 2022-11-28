package ClavarChat;

import ClavarChat.Controllers.ClavarChatNetwork.ClavarChatNetwork;
import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Controllers.Managers.ThreadManager;
import ClavarChat.Controllers.Managers.UserManager;
import ClavarChat.Controllers.Modules.DiscoverModule;
import ClavarChat.Controllers.Modules.LoginVerifyModule;
import ClavarChat.Models.Events.Event.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.ClavarChatMessage.*;
import ClavarChat.Models.Events.NetworkPaquetEvent;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Log.Log;

//DEBUG
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;

import java.util.ArrayList;

public class ClavarChatAPI implements Listener
{
    private int tcpPort;
    private int udpPort;

    private EventManager eventManager;
    private NetworkManager networkManager;
    private ThreadManager threadManager;
    private UserManager userManager;

    private ClavarChatNetwork clavarChatNetwork;

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

        this.clavarChatNetwork = new ClavarChatNetwork(this.threadManager, this.networkManager, this.tcpPort, this.udpPort);

        this.discoverModule = new DiscoverModule(this.networkManager, this.userManager);
        this.loginVerifyModule = new LoginVerifyModule(this.networkManager, this.userManager);

        this.discoverModule.setNext(this.loginVerifyModule);

        this.eventManager.addListenner(this, EVENT_TYPE.EVENT_NETWORK_PAQUET);

        this.clavarChatNetwork.startServer();

        this.DEBUG();
    }

    private void DEBUG()
    {
        ModuleCLI moduleCLI = new ModuleCLI();

        moduleCLI.addCommand("send", () -> {
            String ip = moduleCLI.getUserInput("IP : ");
            String input = "";

            while (!input.equals("q"))
            {
                input = moduleCLI.getUserInput("");
                this.sendMessage(input, ip);
            }
        });

        moduleCLI.addCommand("discover", () -> {
            ArrayList<String> broadcasts =  this.clavarChatNetwork.getBroadcastAddresses();

            for (String addresse : broadcasts) this.clavarChatNetwork.sendUDP(addresse, this.udpPort, new DiscoverMessage());
        });

        moduleCLI.addCommand("login", () -> {
            String pseudo = moduleCLI.getUserInput("Pseudo : ");
            String id = moduleCLI.getUserInput("ID : ");
            this.login(pseudo, id);
        });

        CLI.installModule("api", moduleCLI);
    }

    public boolean login(String pseudo, String id)
    {
        this.userManager.setUser(pseudo, id);
        this.discoverModule.handle();

        return false;
    }

    public void sendMessage(String message, String ip)
    {
        if (this.userManager.isLogged())
        {
            UserData user = this.userManager.getUser();
            TextMessage mgs = new TextMessage(user, message);
            this.clavarChatNetwork.sendTCP(ip, this.tcpPort, mgs);
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
            case EVENT_NETWORK_PAQUET:
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
//            this.networkManager.sendTCP(informationMessage, src);
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