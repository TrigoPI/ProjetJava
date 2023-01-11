package ClavarChat.Models.ClvcNetworkMessage;

public class WaitMessage extends ClvcNetworkMessage
{
    public final static String WAIT = "WAIT";
    public final static String WAIT_FINISHED = "WAIT_FINISHED";

    public WaitMessage(String type)
    {
        super(type);
    }
}
