package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager
{
    private static EventManager instance = null;

    private HashMap<EVENT_TYPE, ArrayList<Listener>> listennersMap;

    private EventManager()
    {
        this.listennersMap = new HashMap<EVENT_TYPE, ArrayList<Listener>>();
    }

    public void addEvent(EVENT_TYPE eventType)
    {
        if (!this.listennersMap.containsKey(eventType))
        {
            this.listennersMap.put(eventType, new ArrayList<Listener>());
        }
        else
        {
            Log.Warning("Event : " + eventType + " already registered");
        }
    }

    public void addListenner(Listener listener, EVENT_TYPE eventType)
    {
        if (!this.listennersMap.containsKey(eventType))
        {
            Log.Warning("Event : " + eventType + " not registered");
        }
        else
        {
            this.listennersMap.get(eventType).add(listener);
        }
    }

    public void notiy(Event event)
    {
        if (!this.listennersMap.containsKey(event.type))
        {
            Log.Warning("Event : " + event.type + " not registered");
        }
        else
        {
            ArrayList<Listener> listeners = this.listennersMap.get(event.type);

            for (int i = 0; i < listeners.size(); i++)
            {
                listeners.get(i).onEvent(event);
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
