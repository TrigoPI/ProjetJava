package ClavarChat.Models.ClvcNetworkMessage;

public class LoginMessage extends ClvcNetworkMessage
{
    public static final String LOGIN  = "LOGIN";
    public static final String LOGOUT = "LOGOUT";

    public static final String PSEUDO = "PSEUDO";

    public final String pseudo;
    public final int id;
    public final byte[] img;

    public LoginMessage(String type, String pseudo, int id, byte[] img)
    {
        super(type);
        this.pseudo = pseudo;
        this.id = id;
        this.img = img;
    }

    public LoginMessage(String type, String pseudo, int id)
    {
        super(type);
        this.pseudo = pseudo;
        this.id = id;
        this.img = null;
    }
}
