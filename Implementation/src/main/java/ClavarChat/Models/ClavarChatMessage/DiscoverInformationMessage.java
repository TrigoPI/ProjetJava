package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.ClavarChatMessage.Enums.MESSAGE_TYPE;
import ClavarChat.Models.Users.UserData;

public class DiscoverInformationMessage extends ClavarChatMessage
{
    public UserData user;
    public int userCount;

    public DiscoverInformationMessage(UserData user, int userCount)
    {
        super(MESSAGE_TYPE.DISCOVER_INFORMATION);
        this.user = user;
        this.userCount = userCount;
    }
}
