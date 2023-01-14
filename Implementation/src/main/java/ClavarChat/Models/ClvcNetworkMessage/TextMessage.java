package ClavarChat.Models.ClvcNetworkMessage;

public class TextMessage extends ClvcNetworkMessage
{
    public static final String TEXT_NETWORK_MESSAGE  = "TEXT_NETWORK_MESSAGE";

    public final String sharedId;
    public final String message;
    public final int userId;

    public TextMessage(int userId, String sharedId, String message)
    {
        super(TEXT_NETWORK_MESSAGE);
        this.userId = userId;
        this.sharedId = sharedId;
        this.message = message;
    }
}
