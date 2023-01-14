package ClavarChat.Models.Message;

public class Message
{
    public final String sharedId;
    public final int messageId;
    public final int userId;
    public final String text;

    public Message(int userId, int messageId, String shareId, String text)
    {
        this.sharedId = shareId;
        this.userId = userId;
        this.messageId = messageId;
        this.text = text;
    }
}
