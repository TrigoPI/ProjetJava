package ClavarChat.Models.ClvcMessage;

public class TextMessage extends ClvcMessage
{
    public static final String TEXT_MESSAGE = "TEXT_MESSAGE";

    public final String sharedId;
    public final String pseudo;
    public final String message;
    public final int id;

    public TextMessage(int id, String sharedId, String pseudo, String message)
    {
        super(TEXT_MESSAGE);
        this.sharedId = sharedId;
        this.pseudo = pseudo;
        this.message = message;
        this.id = id;
    }
}
