package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.CONNECTION_EVENT_TYPE;

public class EndConnectionEvent extends ConnectionEvent
{
    public String ip;

    public EndConnectionEvent(String ip)
    {
        super(CONNECTION_EVENT_TYPE.CONNECTION_EVENT_END);
        this.ip = ip;
    }
}
