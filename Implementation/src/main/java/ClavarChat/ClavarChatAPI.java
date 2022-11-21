package ClavarChat;

import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.NetworkMessageEvent;
import ClavarChat.Models.NetworkMessage.NetworkMessage;
import ClavarChat.Utils.Log.Log;

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
        switch (event.data.type)
        {
            case DISCOVER:
//                this.onDiscover();
                break;
        }
    }

//    private void onDiscover(NetworkMessageEvent event)
//    {
//        Log.Print("Discover from : " + event.);
//    }
}
