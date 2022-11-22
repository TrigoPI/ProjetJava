package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;

import java.io.Serializable;

public class NetworkMessageEvent extends Event
{
    public String src;
    public Serializable data;

    public NetworkMessageEvent(Serializable data, String src)
    {
        super(EVENT_TYPE.NETWORK_MESSAGE_EVENT);
        this.src = src;
        this.data = data;
    }
}
