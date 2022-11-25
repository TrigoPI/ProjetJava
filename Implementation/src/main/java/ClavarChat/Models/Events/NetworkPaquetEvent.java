package ClavarChat.Models.Events;

import java.io.Serializable;

public class NetworkPaquetEvent extends Event
{

    public Serializable data;
    public String ip;
    public int port;

    public NetworkPaquetEvent(String ip, int port, Serializable data)
    {
        super(EVENT_TYPE.EVENT_NETWORK_PAQUET);
        this.ip = ip;
        this.port = port;
        this.data = data;
    }
}
