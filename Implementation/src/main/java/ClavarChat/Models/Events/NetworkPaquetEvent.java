package ClavarChat.Models.Events;

import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;

public class NetworkPaquetEvent extends Event
{

    public ClavarChatMessage data;
    public String ip;
    public int port;

    public NetworkPaquetEvent(String ip, int port, ClavarChatMessage data)
    {
        super(EVENT_TYPE.EVENT_NETWORK_PAQUET);
        this.ip = ip;
        this.port = port;
        this.data = data;
    }
}
