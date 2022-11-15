package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;

public class NetworkEvent extends Event
{
    public NETWORK_EVENT_TYPE networkEventType;

    public NetworkEvent(NETWORK_EVENT_TYPE networkEventType)
    {
        super(EVENT_TYPE.NETWORK_EVENT);
        this.networkEventType = networkEventType;
    }
}
