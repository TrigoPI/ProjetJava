package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.ClavarChatMessage.Enums.MESSAGE_TYPE;
import java.io.Serializable;

public class ClavarChatMessage implements Serializable
{
    public MESSAGE_TYPE type;

    public ClavarChatMessage(MESSAGE_TYPE type)
    {
        this.type = type;
    }
}
