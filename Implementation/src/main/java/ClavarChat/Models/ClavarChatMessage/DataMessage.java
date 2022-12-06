package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.Users.User;

public class DataMessage extends ClavarChatMessage
{
    public enum DATA_TYPE { TEXT, FILE }

    public DATA_TYPE dataType;

    public DataMessage(DATA_TYPE dataType, User user)
    {
        super(user, MESSAGE_TYPE.DATA);
        this.dataType = dataType;
    }
}
