package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.ClavarChatMessage.Enums.MESSAGE_TYPE;
import ClavarChat.Models.Users.UserData;

public class LoginMessage extends ClavarChatMessage
{
    UserData user;

    public LoginMessage(UserData user)
    {
        super(MESSAGE_TYPE.LOGIN);
        this.user = user;
    }
}
