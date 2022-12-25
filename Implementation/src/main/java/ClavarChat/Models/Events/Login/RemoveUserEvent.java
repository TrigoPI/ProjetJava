package ClavarChat.Models.Events.Login;

import ClavarChat.Models.Events.Event;

public class RemoveUserEvent extends Event
{
    public static final String REMOVE_USER = "REMOVE_USER";

    public String pseudo;
    public int id;

    public RemoveUserEvent(String pseudo, int id)
    {
        super(REMOVE_USER);
        this.pseudo = pseudo;
        this.id = id;
    }
}
