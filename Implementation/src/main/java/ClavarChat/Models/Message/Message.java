package ClavarChat.Models.Message;

public class Message
{
    public final int userId;
    public final String text;

    public Message(int userId, String text)
    {
        this.userId = userId;
        this.text = text;
    }
}
