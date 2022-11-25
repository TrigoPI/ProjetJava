package ClavarChat.Models.Events;

public class Event
{
    public enum EVENT_TYPE
    {
        EVENT_NETWORK_CONNECTION,
        EVENT_NETWORK_SOCKET_DATA,
        EVENT_NETWORK_PAQUET,

        EVENT_THREAD,
    }


    public EVENT_TYPE type;

    public Event(EVENT_TYPE type)
    {
        this.type = type;
    }
}
