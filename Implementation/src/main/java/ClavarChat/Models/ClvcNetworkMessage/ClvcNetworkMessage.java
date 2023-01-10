package ClavarChat.Models.ClvcNetworkMessage;

import java.io.Serializable;

public class ClvcNetworkMessage implements Serializable
{
    public String type;

    public ClvcNetworkMessage(String type)
    {
        this.type = type;
    }
}
