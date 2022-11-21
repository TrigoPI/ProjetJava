package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;

public class PaquetEvent extends NetworkEvent
{
    public Paquet data;

    public PaquetEvent(Paquet data)
    {
        super(NETWORK_EVENT_TYPE.NETWORK_EVENT_PAQUET);
        this.data = data;
    }
}
