package ClavarChat.Controllers.Managers;

import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Utils.Loggin.Loggin;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager
{
    private static EventManager instance = null;

    private HashMap<EVENT_TYPE, ArrayList<Listenner>> listennersMap;

    private EventManager()
    {
        this.listennersMap = new HashMap<EVENT_TYPE, ArrayList<Listenner>>();
    }

    public void addEvent(EVENT_TYPE eventType)
    {
        if (!this.listennersMap.containsKey(eventType))
        {
            this.listennersMap.put(eventType, new ArrayList<Listenner>());
        }
        else
        {
            Loggin.Warning("Event : " + eventType + " already registered");
        }
    }

    public void addListenner(Listenner listenner, EVENT_TYPE eventType)
    {
        if (!this.listennersMap.containsKey(eventType))
        {
            Loggin.Warning("Event : " + eventType + " not registered");
        }
        else
        {
            this.listennersMap.get(eventType).add(listenner);
        }
    }

    public void notiy(Event event)
    {
        if (!this.listennersMap.containsKey(event.type))
        {
            Loggin.Warning("Event : " + event.type + " not registered");
        }
        else
        {
            ArrayList<Listenner> listenners = this.listennersMap.get(event.type);

            for (int i = 0; i < listenners.size(); i++)
            {
                listenners.get(i).onEvent(event);
            }
        }
    }

    public static EventManager getInstance()
    {
        if (instance != null) return instance;
        instance = new EventManager();
        return instance;
    }
}
