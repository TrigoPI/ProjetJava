package ClavarChat.Models.Events;

import java.io.Serializable;

public class SocketDataEvent extends Event
{
    public Serializable data;
    public String src;
    public int port;

    public SocketDataEvent(String src, int port, Serializable data)
    {
        super(EVENT_TYPE.EVENT_NETWORK_SOCKET_DATA);

        this.src = src;
        this.port = port;
        this.data = data;
    }
}
