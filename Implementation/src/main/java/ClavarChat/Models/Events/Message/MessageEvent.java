package ClavarChat.Models.Events.Message;

import ClavarChat.Models.Events.Event;

public class MessageEvent extends Event
{
    public static final String TEXT_MESSAGE = "TEXT_MESSAGE";

    public String message;

    public MessageEvent()
    {
        super(TEXT_MESSAGE);
    }
}
