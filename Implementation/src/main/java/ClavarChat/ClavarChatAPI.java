package ClavarChat;

import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.ClavarChatMessage.Enums.MESSAGE_TYPE;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.NetworkMessageEvent;
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;

public class ClavarChatAPI implements Listener
{
    private EventManager eventManager;
    private NetworkManager networkManager;

    public ClavarChatAPI(int tcpPort, int udpPort)
    {
        this.eventManager = EventManager.getInstance();
        this.networkManager = new NetworkManager(tcpPort, udpPort);

        this.eventManager.addEvent(EVENT_TYPE.NETWORK_MESSAGE_EVENT);
        this.eventManager.addListenner(this, EVENT_TYPE.NETWORK_MESSAGE_EVENT);

        this.DEBUG();
    }

    private void DEBUG()
    {
        ModuleCLI moduleCLI = new ModuleCLI();

        moduleCLI.addCommand("discover", () -> {
            ArrayList<String> ips = this.networkManager.getBroadcastAddresses();
            for (String ip : ips) this.networkManager.sendUDP(new ClavarChatMessage(MESSAGE_TYPE.DISCOVER), ip);
        });

        CLI.installModule("api", moduleCLI);
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
                this.onDiscover(event);
                break;
        }
    }

    private void onDiscover(NetworkMessageEvent event)
    {
        Log.Print("Discover from : " + event.src);
    }
}
