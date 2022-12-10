package ClavarChat.Models.Events.Network;

import ClavarChat.Models.Events.Event;

public class SocketSendingEndEvent extends Event
{
    public static final String FINISHED_SENDING = "FINISHED_SENDING";

    public String dstIp;
    public int socketId;

    public SocketSendingEndEvent(int socketId, String dstIp)
    {
        super(FINISHED_SENDING);

        this.dstIp = dstIp;
        this.socketId = socketId;
    }
}
