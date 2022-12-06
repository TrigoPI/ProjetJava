package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.Users.User;

public class TextMessage extends DataMessage
{
    public String message;

    public TextMessage(User user, String message)
    {
        super(DATA_TYPE.TEXT, user);
        this.message = message;
    }
}
