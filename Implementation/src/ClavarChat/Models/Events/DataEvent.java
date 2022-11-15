package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;

public class DataEvent extends NetworkEvent
{
    public Paquet data;

    public DataEvent(Paquet data)
    {
        super(NETWORK_EVENT_TYPE.NETWORK_EVENT_DATA);
        this.data = data;
    }
}
