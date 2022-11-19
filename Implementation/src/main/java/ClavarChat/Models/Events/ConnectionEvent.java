package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.CONNECTION_EVENT_TYPE;
import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;

public class ConnectionEvent extends NetworkEvent
{
    public CONNECTION_EVENT_TYPE connectionEventType;

    public ConnectionEvent(CONNECTION_EVENT_TYPE connectionEventType)
    {
        super(NETWORK_EVENT_TYPE.NETWORK_EVENT_CONNECTION);
        this.connectionEventType = connectionEventType;
    }
}
