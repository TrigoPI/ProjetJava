package ClavarChat.Models.Events.Network;

import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.Events.Event;

public class NetworkPacketEvent extends Event
{
    public static final String NETWORK_PACKET = "NETWORK_PAQUET";

    public ClavarChatMessage data;
    public String ip;
    public int port;

    public NetworkPacketEvent(String ip, int port, ClavarChatMessage data)
    {
        super(NETWORK_PACKET);
        this.ip = ip;
        this.port = port;
        this.data = data;
    }
}
