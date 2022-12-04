package ClavarChat.Models.Events;

import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;

public class NetworkPaquetEvent extends Event
{
    public static final String NETWORK_PAQUET = "NETWORK_PAQUET";

    public ClavarChatMessage data;
    public String ip;
    public int port;

    public NetworkPaquetEvent(String ip, int port, ClavarChatMessage data)
    {
        super(NETWORK_PAQUET);
        this.ip = ip;
        this.port = port;
        this.data = data;
    }
}
