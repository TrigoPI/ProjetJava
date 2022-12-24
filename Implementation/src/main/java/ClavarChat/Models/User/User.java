package ClavarChat.Models.User;

import java.io.Serializable;

public class User implements Serializable
{
    public String pseudo;
    public int id;

    public User(String pseudo, int id)
    {
        this.pseudo = pseudo;
        this.id = id;
    }
}
