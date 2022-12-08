package ClavarChat.Models.ClavarChatMessage;

public class DiscoverResponseMessage extends ClavarChatMessage
{
    public static final String DISCOVER_RESPONSE  = "DISCOVER_RESPONSE";

    public int count;
    public String pseudo;
    public String id;

    public DiscoverResponseMessage(String pseudo, String id, int count)
    {
        super(DISCOVER_RESPONSE);

        this.pseudo = pseudo;
        this.count = count;
        this.id = id;
    }
}
