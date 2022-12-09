package ClavarChat.Models.Events.Network;

import ClavarChat.Models.Events.Event;

public class SocketSendingEndEvent extends Event
{
    public static final String FINISHED_SENDING = "FINISHED_SENDING";

    public int socketId;

    public SocketSendingEndEvent(int socketId)
    {
        super(FINISHED_SENDING);
        this.socketId = socketId;
    }
}
