package ClavarChat.Controllers.API;

import ClavarChat.Models.ClvcEvent.ClvcEvent;
import ClavarChat.Models.ClvcListener.ClvcListener;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class EventAPI
{
    private HashMap<String, ArrayList<ClvcListener>> eventsMap;

    public EventAPI()
    {
        this.eventsMap = new HashMap<>();
    }

    public void addListener(ClvcListener listener, String eventName)
    {
        if (!this.eventsMap.containsKey(eventName))
        {
            this.eventsMap.put(eventName, new ArrayList<>());
        }

        ArrayList<ClvcListener> listeners = this.eventsMap.get(eventName);

        if (!listeners.contains(listener))
        {
            listeners.add(listener);
        }
        else
        {
            Log.Warning(EventAPI.class.getName() + " Listener already registered");
        }

    }

    public void notify(ClvcEvent event)
    {
        if (!this.eventsMap.containsKey(event.type))
        {
            Log.Warning(EventAPI.class.getName() + " No event : " + event.type);
            return;
        }

        ArrayList<ClvcListener> listeners = this.eventsMap.get(event.type);

        for (ClvcListener listener : listeners) listener.onEvent(event);
    }
}
