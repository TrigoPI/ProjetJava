package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.ByteImage.ByteImage;

public class LoginMessage extends ClavarChatMessage
{
    public static final String LOGIN  = "LOGIN";
    public static final String LOGOUT = "LOGOUT";

    public String pseudo;
    public String id;
    public ByteImage img;

    public LoginMessage(String type, String pseudo, String id, ByteImage img)
    {
        super(type);
        this.pseudo = pseudo;
        this.id = id;
        this.img = img;
    }

    public LoginMessage(String type, String pseudo, String id)
    {
        super(type);
        this.pseudo = pseudo;
        this.id = id;
        this.img = null;
    }
}
