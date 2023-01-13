package ClavarChat.Models.ClvcNetworkMessage;

public class TypingMessage extends ClvcNetworkMessage
{
    public final static String TYPING_START      = "TYPING_START";
    public final static String TYPING_END        = "TYPING_END";

    public final String sharedId;
    public final int userId;

    public TypingMessage(String type, int userId, String sharedId)
    {
        super(type);
        this.userId = userId;
        this.sharedId = sharedId;
    }
}
