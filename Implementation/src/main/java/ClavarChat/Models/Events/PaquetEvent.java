package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;

import java.io.Serializable;

public class PaquetEvent extends NetworkEvent
{
    public String dst;
    public String src;
    public Serializable data;

    public PaquetEvent(Serializable data, String dst, String src)
    {
        super(NETWORK_EVENT_TYPE.NETWORK_EVENT_PAQUET);
        this.dst = dst;
        this.src = src;
        this.data = data;
    }
}
