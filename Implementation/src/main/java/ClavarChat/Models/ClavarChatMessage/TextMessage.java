package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.ClavarChatMessage.Enums.DATA_MESSAGE_TYPE;
import ClavarChat.Models.Users.UserData;

public class TextMessage extends DataMessage
{
    public String message;

    public TextMessage(UserData user, String message)
    {
        super(user, DATA_MESSAGE_TYPE.TEXT);
        this.message = message;
    }
}
