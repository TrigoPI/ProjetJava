package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;

import java.io.Serializable;

public class PaquetEvent extends NetworkEvent
{
    public String src;
    public int port;
    public Serializable data;

    public PaquetEvent(Serializable data, String src, int port)
    {
        super(NETWORK_EVENT_TYPE.NETWORK_EVENT_PAQUET);
        this.src = src;
        this.port = port;
        this.data = data;
    }
}
