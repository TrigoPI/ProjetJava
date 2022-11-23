package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.ClavarChatMessage.Enums.DATA_MESSAGE_TYPE;
import ClavarChat.Models.ClavarChatMessage.Enums.MESSAGE_TYPE;
import ClavarChat.Models.Users.UserData;

public class DataMessage extends ClavarChatMessage
{
    public DATA_MESSAGE_TYPE dataType;
    public UserData user;

    public DataMessage(UserData user, DATA_MESSAGE_TYPE dataType)
    {
        super(MESSAGE_TYPE.DATA);
        this.user = user;
        this.dataType = dataType;
    }
}
