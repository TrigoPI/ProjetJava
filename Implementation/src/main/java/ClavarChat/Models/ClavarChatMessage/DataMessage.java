package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.Users.UserData;

public class DataMessage extends ClavarChatMessage
{
    public enum DATA_TYPE { TEXT, FILE }

    public DATA_TYPE dataType;

    public DataMessage(DATA_TYPE dataType, UserData user)
    {
        super(user, MESSAGE_TYPE.DATA);
        this.dataType = dataType;
    }
}
