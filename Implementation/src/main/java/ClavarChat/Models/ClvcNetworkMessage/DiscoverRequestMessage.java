package ClavarChat.Models.ClvcNetworkMessage;

public class DiscoverRequestMessage extends ClvcNetworkMessage
{
    public static final String DISCOVER_REQUEST  = "DISCOVER_REQUEST";

    public DiscoverRequestMessage()
    {
        super(DISCOVER_REQUEST);
    }
}
