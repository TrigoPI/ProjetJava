package ClavarChat.Models.ChainData.Request;

public class LoginRequest implements Request
{
    public String pseudo;
    public String id;
    public String password;

    public LoginRequest(String pseudo, String id, String password)
    {
        this.pseudo = pseudo;
        this.id = id;
        this.password = password;
    }
}
