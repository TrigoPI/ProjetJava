package ClavarChat.Models.Events;

import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;

public class SocketDataEvent extends Event
{
    public ClavarChatMessage data;
    public String src;
    public int port;

    public SocketDataEvent(String src, int port, ClavarChatMessage data)
    {
        super(EVENT_TYPE.EVENT_NETWORK_SOCKET_DATA);

        this.src = src;
        this.port = port;
        this.data = data;
    }
}
