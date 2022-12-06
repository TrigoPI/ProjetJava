package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.Users.User;

import java.io.Serializable;

public class ClavarChatMessage implements Serializable
{
    public enum MESSAGE_TYPE { DISCOVER, DATA, LOGIN, LOGOUT}

    public MESSAGE_TYPE type;
    public User user;

    public ClavarChatMessage(User user, MESSAGE_TYPE type)
    {
        this.user = user;
        this.type = type;
    }

    public ClavarChatMessage(MESSAGE_TYPE type)
    {
        this.type = type;
        this.user = null;
    }
}
