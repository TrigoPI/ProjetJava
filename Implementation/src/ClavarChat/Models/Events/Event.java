package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.EVENT_TYPE;

public class Event
{
    public EVENT_TYPE type;

    public Event(EVENT_TYPE type)
    {
        this.type = type;
    }
}
