package ClavarChat.Controllers.Managers.Event;

import ClavarChat.Models.Events.Event;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager
{
    private static EventManager instance = null;

    private HashMap<String, ArrayList<Listener>> listenners;

    private EventManager()
    {
        this.listenners = new HashMap<>();
    }

    public void addEvent(String eventType)
    {
        if (!this.listenners.containsKey(eventType))
        {
            this.listenners.put(eventType, new ArrayList<Listener>());
            Log.Print(this.getClass().getName() + " Event " + eventType);
        }
        else
        {
            Log.Warning(this.getClass().getName() + " Event : " + eventType + " already registered");
        }
    }

    public void addListenner(Listener listener, String eventType)
    {
        if (!this.listenners.containsKey(eventType))
        {
            Log.Print(this.getClass().getName() + " Event : " + eventType + " not registered");
        }
        else
        {
            this.listenners.get(eventType).add(listener);
        }
    }

    public void notiy(Event event)
    {
        if (!this.listenners.containsKey(event.type))
        {
            Log.Warning(this.getClass().getName() + " Event : " + event.type + " not registered");
        }
        else
        {
            for (Listener listener : this.listenners.get(event.type)) listener.onEvent(event);
        }
    }

    public static EventManager getInstance()
    {
        if (instance != null) return instance;
        instance = new EventManager();
        return instance;
    }
}
