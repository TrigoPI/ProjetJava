package ClavarChat.Models.Events.Message;

import ClavarChat.Models.Events.Event;

public class MessageEvent extends Event
{
    public static final String TEXT_MESSAGE = "TEXT_MESSAGE";

    public String pseudo;
    public String id;
    public String message;

    public MessageEvent(String pseudo, String id, String message)
    {
        super(TEXT_MESSAGE);

        this.message = message;
        this.pseudo = pseudo;
        this.id = id;
    }
}