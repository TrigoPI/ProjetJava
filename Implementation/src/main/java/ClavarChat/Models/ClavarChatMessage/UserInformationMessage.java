package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.ClavarChatMessage.Enums.MESSAGE_TYPE;
import ClavarChat.Models.Users.UserData;

public class UserInformationMessage extends ClavarChatMessage
{
    public UserData user;
    public int userCount;

    public UserInformationMessage(UserData user, int userCount)
    {
        super(MESSAGE_TYPE.INFORMATION);
        this.user = user;
        this.userCount = userCount;
    }
}
