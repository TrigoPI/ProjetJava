package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.ClavarChatMessage.Enums.MESSAGE_TYPE;
import ClavarChat.Models.Users.UserData;

public class UserInformationMessage extends ClavarChatMessage
{
    public UserData user;

    public UserInformationMessage(UserData user)
    {
        super(MESSAGE_TYPE.INFORMATION);
        this.user = user;
    }
}
