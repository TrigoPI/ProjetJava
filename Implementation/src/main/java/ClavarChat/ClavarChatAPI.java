package ClavarChat;

import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Controllers.Managers.UserManager;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.ClavarChatMessage.DataMessage;
import ClavarChat.Models.ClavarChatMessage.Enums.MESSAGE_TYPE;
import ClavarChat.Models.ClavarChatMessage.TextMessage;
import ClavarChat.Models.ClavarChatMessage.UserInformationMessage;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.NetworkMessageEvent;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;

public class ClavarChatAPI implements Listener
{
    private EventManager eventManager;
    private NetworkManager networkManager;
    private UserManager userManager;

    public ClavarChatAPI(int tcpPort, int udpPort)
    {
        this.eventManager = EventManager.getInstance();
        this.userManager = new UserManager();
        this.networkManager = new NetworkManager(tcpPort, udpPort);

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

        CLI.installModule("api", moduleCLI);
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
            Log.Warning("User not logged");
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
            case INFORMATION:
                this.onInformation((UserInformationMessage)data, event.src);
                break;
            case DATA:
                this.onData((DataMessage)data, event.src);
                break;
        }
    }

    private void onDiscover(ClavarChatMessage data, String src)
    {
        Log.Print("Discover from : " + src);

        if (this.userManager.isLogged())
        {
            UserData user = this.userManager.getUser();
            this.networkManager.sendTCP(new UserInformationMessage(user), src);
        }
        else
        {
            Log.Warning("User not logged cannot respond to DISCOVER");
        }
    }

    private void onInformation(UserInformationMessage data, String src)
    {
        this.userManager.addUser(data.user, src);
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
        Log.Print("Message from " + src + " : " + data.message);
    }

    private void discover()
    {
        ArrayList<String> broadcast = this.networkManager.getBroadcastAddresses();
        for (String addresse : broadcast) this.networkManager.sendUDP(new ClavarChatMessage(MESSAGE_TYPE.DISCOVER), addresse);
    }
}
