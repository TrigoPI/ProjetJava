package ClavarChat.Models.ClvcNetworkMessage;

import java.io.Serializable;

public class ClvcNetworkMessage implements Serializable
{
    public final static String WAIT              = "WAIT";
    public final static String WAIT_FINISHED     = "WAIT_FINISHED";
    public static final String DISCOVER_REQUEST  = "DISCOVER_REQUEST";
    public final static String TYPING_START      = "TYPING_START";
    public final static String TYPING_END        = "TYPING_END";

    public String type;

    public ClvcNetworkMessage(String type)
    {
        this.type = type;
    }
}
