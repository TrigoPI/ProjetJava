package ClavarChat;

import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.NetworkMessageEvent;
import ClavarChat.Models.NetworkMessage.NetworkMessage;
import ClavarChat.Models.Paquets.Paquet;
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
        Log.Print("ClavarChatAPI Event --> " + event.type);

        switch (event.type)
        {
            case NETWORK_MESSAGE_EVENT:
                this.onNetworkMessageEvent((NetworkMessageEvent)event);
                break;
        }
    }

    private void onNetworkMessageEvent(NetworkMessageEvent paquetEvent)
    {
        Log.Print("CalvarChatAPI PaquetType --> " + paquetEvent.data.type);

//        switch (paquetEvent.data.type)
//        {
//            case PAQUET_LOGIN:
//                this.onLoginPaquet(paquetEvent.data);
//                break;
//            case PAQUET_LOGOUT:
//                break;
//            case PAQUET_DISCOVER:
//                break;
//            case PAQUET_USER_INFORMATION:
//                break;
//        }
    }

//    private void onLoginPaquet(Paquet paquet)
//    {
//        Log.Print("User : " + paquet.user.pseudo + "/" + paquet.user.id + "/" + paquet.src);
//    }
}
