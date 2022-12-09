package ClavarChat.Models.ClavarChatMessage;

public class TextMessage extends ClavarChatMessage
{
    public static final String TEXT_MESSAGE = "TEXT_MESSAGE";

    public String pseudo;
    public String message;
    public String id;

    public TextMessage(String pseudo, String id, String message)
    {
        super(TEXT_MESSAGE);
        this.pseudo = pseudo;
        this.message = message;
        this.id = id;
    }
}
