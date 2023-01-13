package ClavarChat.Models.ClvcEvent;

public class TypingEvent extends ClvcEvent
{
    public static final String TYPING = "TYPING";

    public final boolean isTyping;
    public final String sharedId;
    public final int userId;

    public TypingEvent(int userId , String sharedId, boolean isTyping)
    {
        super(TYPING);

        this.userId   = userId;
        this.sharedId = sharedId;
        this.isTyping = isTyping;
    }
}
