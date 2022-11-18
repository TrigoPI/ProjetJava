package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;

public class EndConnectionEvent extends NetworkEvent
{
    public String ip;

    public EndConnectionEvent(String ip)
    {
        super(NETWORK_EVENT_TYPE.NETWORK_EVENT_END_CONNECTION);
        this.ip = ip;
    }
}
