package ClavarChat.Models.Paquets;

import ClavarChat.Models.Paquets.Enums.PAQUET_TYPE;
import ClavarChat.Models.Users.UserData;

import java.io.Serializable;

public class Paquet implements Serializable
{
    public UserData user;
    public PAQUET_TYPE type;
    public String dst;
    public String src;

    public Paquet(UserData user, PAQUET_TYPE type, String dst)
    {
        this.user = user;
        this.type = type;
        this.dst = dst;
    }
}
