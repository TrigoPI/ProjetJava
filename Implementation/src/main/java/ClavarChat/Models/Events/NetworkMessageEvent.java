package ClavarChat.Models.Events;

import ClavarChat.Models.NetworkMessage.NetworkMessage;

public class NetworkMessageEvent
{
    public String src;
    public NetworkMessage data;

    public NetworkMessageEvent(String src, NetworkMessage data)
    {
        this.src = src;
        this.data = data;
    }
}
