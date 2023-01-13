package ClavarChat.Models.ClvcEvent;

public class TypingEvent extends ClvcEvent
{
    public static final String TYPING = "TYPING";

    public final boolean isTyping;
    public final String sharedId;

    public TypingEvent(String sharedId, boolean isTyping)
    {
        super(TYPING);

        this.sharedId = sharedId;
        this.isTyping = isTyping;
    }
}
