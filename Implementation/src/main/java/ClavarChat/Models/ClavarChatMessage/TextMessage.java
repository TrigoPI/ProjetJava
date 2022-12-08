package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.Users.User;

public class TextMessage extends ClavarChatMessage
{
    public static final String TEXT_MESSAGE = "TEXT_MESSAGE";
//
    public String message;
//
    public TextMessage(User user, String message)
    {
        super(TEXT_MESSAGE);
        this.message = message;
    }
}
