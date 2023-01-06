package ClavarChat.Models.ClvcEvent.Message;

import ClavarChat.Models.ClvcEvent.ClvcEvent;

public class MessageEvent extends ClvcEvent
{
    public static final String TEXT_MESSAGE = "TEXT_MESSAGE";

    public String sharedId;
    public String pseudo;
    public String message;
    public int id;

    public MessageEvent(String sharedId, String pseudo, int id, String message)
    {
        super(TEXT_MESSAGE);

        this.sharedId = sharedId;
        this.message = message;
        this.pseudo = pseudo;
        this.id = id;
    }
}
