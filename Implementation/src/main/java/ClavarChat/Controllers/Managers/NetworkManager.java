package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Threads.TCPServerThread;
import ClavarChat.Models.Events.DataEvent;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.NetworkEvent;
import ClavarChat.Models.Events.PaquetEvent;
import ClavarChat.Utils.Loggin.Loggin;

public class NetworkManager implements Listener
{
    private EventManager eventManager;

    public NetworkManager()
    {
        this.eventManager = EventManager.getInstance();

        this.eventManager.addEvent(EVENT_TYPE.NETWORK_EVENT);
        this.eventManager.addListenner(this, EVENT_TYPE.NETWORK_EVENT);
    }

    @Override
    public void onEvent(Event event)
    {
        Loggin.Print("NetworkManager Event --> " + event.type);

        switch (event.type)
        {
            case NETWORK_EVENT:
                this.onNetworkEvent((NetworkEvent)event);
                break;
        }
    }

    private void onNetworkEvent(NetworkEvent event)
    {
        switch (event.networkEventType)
        {
            case NETWORK_EVENT_DATA:
                Loggin.Print("NetworkManager Event --> " + event.networkEventType);
                this.onNetworkEventData((DataEvent)event);
                break;
            case NETWORK_EVENT_CONNECTION_SUCCESS:
                break;
            case NETWORK_EVENT_END_CONNECTION:
                break;
            case NETWORK_EVENT_NEW_CONNECTION:
                break;
        }
    }

    private void onNetworkEventData(DataEvent event)
    {
        this.eventManager.notiy(new PaquetEvent(event.data));
    }
}
