package ClavarChat.Models.Users;

import java.io.Serializable;

public class User implements Serializable
{
    public String pseudo;
    public String id;

    public User(String pseudo, String id)
    {
        this.pseudo = pseudo;
        this.id = id;
    }
}
