package ClavarChat.Models.Users;

import java.io.Serializable;

public class UserData implements Serializable
{
    public String pseudo;
    public String id;
    public String ip;

    public UserData(String pseudo, String id, String ip)
    {
        this.pseudo = pseudo;
        this.id = id;
        this.ip = ip;
    }
}
