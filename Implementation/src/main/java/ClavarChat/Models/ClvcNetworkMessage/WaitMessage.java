package ClavarChat.Models.ClvcNetworkMessage;

public class WaitMessage extends ClvcNetworkMessage
{
    public final static String WAIT = "WAIT";

    public WaitMessage()
    {
        super(WAIT);
    }
}
