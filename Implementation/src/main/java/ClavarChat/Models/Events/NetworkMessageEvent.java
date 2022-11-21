package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.NetworkMessage.NetworkMessage;

public class NetworkMessageEvent extends Event
{
    public String src;
    public NetworkMessage data;

    public NetworkMessageEvent(String src, NetworkMessage data)
    {
        super(EVENT_TYPE.NETWORK_MESSAGE_EVENT);
        this.src = src;
        this.data = data;
    }
}
