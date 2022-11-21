package ClavarChat.Models.Paquets;

import ClavarChat.Models.NetworkMessage.NetworkMessage;
import ClavarChat.Models.Users.UserData;

import java.io.Serializable;

public class Paquet implements Serializable
{
    public String dst;
    public String src;
    public NetworkMessage data;

    public Paquet(NetworkMessage data, String dst)
    {
        this.data = data;
        this.dst = dst;
        this.src = "";
    }
}
