package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;

public class PaquetEvent extends Event
{
    public Paquet data;

    public PaquetEvent(Paquet data)
    {
        super(EVENT_TYPE.PAQUET_EVENT);
        this.data = data;
    }
}
