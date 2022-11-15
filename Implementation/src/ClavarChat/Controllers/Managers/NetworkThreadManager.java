package ClavarChat.Controllers.Managers;

import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;

public class NetworkThreadManager implements Listenner
{
    private EventManager eventManager;

    public NetworkThreadManager()
    {
        this.eventManager = EventManager.getInstance();

        this.eventManager.addEvent(EVENT_TYPE.THREAD_EVENT);
        this.eventManager.addListenner(this, EVENT_TYPE.THREAD_EVENT);
    }

    @Override
    public void onEvent(Event event)
    {

    }
}
