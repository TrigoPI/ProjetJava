package ClavarChat.Models.ClavarChatMessage;

public class LoginMessage extends ClavarChatMessage
{
    public static final String LOGIN  = "LOGIN";
    public static final String LOGOUT = "LOGOUT";

    public String pseudo;
    public String id;

    public LoginMessage(String type, String pseudo, String id)
    {
        super(type);
        this.pseudo = pseudo;
        this.id = id;
    }
}
