package ClavarChat.Models.ClavarChatMessage;

import javafx.scene.image.Image;

public class DiscoverResponseMessage extends ClavarChatMessage
{
    public static final String DISCOVER_RESPONSE  = "DISCOVER_RESPONSE";

    public int count;
    public Image avatar;
    public String pseudo;
    public String id;

    public DiscoverResponseMessage(String pseudo, String id, Image avatar, int count)
    {
        super(DISCOVER_RESPONSE);

        this.avatar = avatar;
        this.pseudo = pseudo;
        this.count = count;
        this.id = id;
    }
}
