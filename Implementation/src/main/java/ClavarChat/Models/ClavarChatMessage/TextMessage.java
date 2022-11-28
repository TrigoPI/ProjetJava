package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.Users.UserData;

public class TextMessage extends DataMessage
{
    public String message;

    public TextMessage(UserData user, String message)
    {
        super(DATA_TYPE.TEXT, user);
        this.message = message;
    }
}
