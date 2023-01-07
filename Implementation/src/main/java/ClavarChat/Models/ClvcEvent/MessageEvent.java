package ClavarChat.Models.ClvcEvent;

public class MessageEvent extends ClvcEvent
{
    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";

    public MessageEvent()
    {
        super(MESSAGE_EVENT);
    }
}
