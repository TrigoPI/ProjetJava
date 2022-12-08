package ClavarChat.Models.ClavarChatMessage;

import java.io.Serializable;

public class ClavarChatMessage implements Serializable
{
    public String type;

    public ClavarChatMessage(String type)
    {
        this.type = type;
    }
}
