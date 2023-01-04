package ClavarChat.Models.ClvcMessage;

import java.io.Serializable;

public class ClvcMessage implements Serializable
{
    public String type;

    public ClvcMessage(String type)
    {
        this.type = type;
    }
}
