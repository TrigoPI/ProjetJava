package ClavarChat.Models.ClvcMessage;

public class DiscoverResponseMessage extends ClvcMessage
{
    public static final String DISCOVER_RESPONSE  = "DISCOVER_RESPONSE";

    public int count;
    public byte[] avatar;
    public String pseudo;
    public int id;

    public DiscoverResponseMessage(String pseudo, int id, byte[] avatar, int count)
    {
        super(DISCOVER_RESPONSE);

        this.avatar = avatar;
        this.pseudo = pseudo;
        this.count = count;
        this.id = id;
    }
}
