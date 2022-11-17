package ClavarChat;

import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.PaquetEvent;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Log.Log;

public class ClavarChatAPI implements Listener
{
    private EventManager eventManager;

    public ClavarChatAPI()
    {
        this.eventManager = EventManager.getInstance();

        this.eventManager.addEvent(EVENT_TYPE.PAQUET_EVENT);
        this.eventManager.addListenner(this, EVENT_TYPE.PAQUET_EVENT);
    }

    @Override
    public void onEvent(Event event)
    {
        Log.Print("ClavarChatAPI Event --> " + event.type);

        switch (event.type)
        {
            case PAQUET_EVENT:
                onPaquetEvent((PaquetEvent)event);
                break;
        }
    }

    private void onPaquetEvent(PaquetEvent paquetEvent)
    {
        Log.Print("CalvarChatAPI PaquetType --> " + paquetEvent.data.type);

        switch (paquetEvent.data.type)
        {
            case PAQUET_LOGIN:
                this.onLoginPaquet(paquetEvent.data);
                break;
            case PAQUET_LOGOUT:
                break;
            case PAQUET_DISCOVER:
                break;
            case PAQUET_USER_INFORMATION:
                break;
        }
    }

    private void onLoginPaquet(Paquet paquet)
    {
        Log.Print("User : " + paquet.user.pseudo + "/" + paquet.user.id + "/" + paquet.user.ip);
    }
}
