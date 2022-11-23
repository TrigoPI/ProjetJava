package ClavarChat;

import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Controllers.Managers.UserManager;
import ClavarChat.Controllers.Modules.DiscoverModule;
import ClavarChat.Models.ClavarChatMessage.*;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.NetworkMessageEvent;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Log.Log;

//DEBUG
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;

import java.util.ArrayList;

public class ClavarChatAPI implements Listener
{
    private EventManager eventManager;
    private NetworkManager networkManager;
    private UserManager userManager;

    private DiscoverModule discoverModule;

    public ClavarChatAPI(int tcpPort, int udpPort)
    {
        this.eventManager = EventManager.getInstance();
        this.userManager = new UserManager();
        this.networkManager = new NetworkManager(tcpPort, udpPort);

        this.discoverModule = new DiscoverModule(this.networkManager);

        this.eventManager.addEvent(EVENT_TYPE.NETWORK_MESSAGE_EVENT);
        this.eventManager.addListenner(this, EVENT_TYPE.NETWORK_MESSAGE_EVENT);

        this.DEBUG();
    }

    private void DEBUG()
    {
        ModuleCLI moduleCLI = new ModuleCLI();

        moduleCLI.addCommand("discover", () -> {
            this.discover();
        });

        moduleCLI.addCommand("send", () -> {
            String ip = moduleCLI.getUserInput("IP : ");
            String input = "";

            while (!input.equals("q"))
            {
                input = moduleCLI.getUserInput("");
                this.sendMessage(input, ip);
            }
        });

        moduleCLI.addCommand("login", () -> {
            String pseudo = moduleCLI.getUserInput("Pseudo : ");
            String id = moduleCLI.getUserInput("ID : ");
            this.login(pseudo, id);
        });

        CLI.installModule("api", moduleCLI);
    }

    public void login(String pseudo, String id)
    {
        if (this.discover())
        {
            if (!this.userManager.userExist(pseudo))
            {
                this.userManager.setUser(pseudo, id);

                ArrayList<UserData> users = this.userManager.getUsers();
                UserData mainUser = this.userManager.getUser();

                for (UserData user : users)
                {
                    LoginMessage loginMessage = new LoginMessage(mainUser);
                    ArrayList<String> ip = this.userManager.getUserIP(user.pseudo);
                    this.networkManager.sendTCP(loginMessage, ip.get(0));
                }
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " Error discover");
        }
    }

    public void sendMessage(String message, String ip)
    {
        if (this.userManager.isLogged())
        {
            UserData user = this.userManager.getUser();
            TextMessage mgs = new TextMessage(user, message);
            this.networkManager.sendTCP(mgs, ip);
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
            case NETWORK_MESSAGE_EVENT:
                this.onNetworkMessageEvent((NetworkMessageEvent)event);
                break;
        }
    }

    private void onNetworkMessageEvent(NetworkMessageEvent event)
    {
        ClavarChatMessage data = (ClavarChatMessage)event.data;

        switch (data.type)
        {
            case DISCOVER:
                this.onDiscover(data, event.src);
                break;
            case DISCOVER_INFORMATION:
                this.onDiscoverInformation((DiscoverInformationMessage)data, event.src);
                break;
            case DATA:
                this.onData((DataMessage)data, event.src);
                break;
        }
    }

    private void onDiscover(ClavarChatMessage data, String src)
    {
        Log.Print(this.getClass().getName() + " Discover from : " + src);

        if (this.userManager.isLogged())
        {
            int count = this.userManager.getUserCount();
            UserData user = this.userManager.getUser();
            DiscoverInformationMessage informationMessage = new DiscoverInformationMessage(user, count);
            this.networkManager.sendTCP(informationMessage, src);
        }
        else
        {
            Log.Error(this.getClass().getName() + " User not logged cannot respond to DISCOVER");
        }
    }

    private void onDiscoverInformation(DiscoverInformationMessage data, String src)
    {
        Log.Info(this.getClass().getName() + " Discover information from user : " + data.user.pseudo + " / " + "#" + data.user.id);
        this.userManager.addUser(data.user, src);
        this.discoverModule.onDiscoverInformation(data);
    }

    private void onData(DataMessage data, String src)
    {
        switch (data.dataType)
        {
            case TEXT:
                this.onTextMessage((TextMessage)data, src);
                break;
        }
    }

    private void onTextMessage(TextMessage data, String src)
    {
        Log.Info(this.getClass().getName() + " Message from " + src + " : " + data.message);
    }

    private boolean discover()
    {
        this.discoverModule.discover();
        return this.discoverModule.succeed();
    }
}