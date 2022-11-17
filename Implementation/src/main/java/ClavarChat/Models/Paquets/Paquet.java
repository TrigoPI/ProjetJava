package ClavarChat.Models.Paquets;

import ClavarChat.Models.Paquets.Enums.PAQUET_TYPE;
import ClavarChat.Models.Users.UserData;

public class Paquet
{
    public UserData user;
    public PAQUET_TYPE type;

    public Paquet(UserData user, PAQUET_TYPE type)
    {
        this.user = user;
        this.type = type;
    }
}
