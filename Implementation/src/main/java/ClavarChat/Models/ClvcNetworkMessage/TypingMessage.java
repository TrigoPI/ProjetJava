package ClavarChat.Models.ClvcNetworkMessage;

public class TypingMessage extends ClvcNetworkMessage
{
    public final static String TYPING_START      = "TYPING_START";
    public final static String TYPING_END        = "TYPING_END";

    public final String sharedId;

    public TypingMessage(String type, String sharedId)
    {
        super(type);
        this.sharedId = sharedId;
    }
}
