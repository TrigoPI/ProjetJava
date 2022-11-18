package ClavarChat.Models.Users;

import java.io.Serializable;

public class UserData implements Serializable
{
    public String pseudo;
    public String id;

    public UserData(String pseudo, String id)
    {
        this.pseudo = pseudo;
        this.id = id;
    }
}
