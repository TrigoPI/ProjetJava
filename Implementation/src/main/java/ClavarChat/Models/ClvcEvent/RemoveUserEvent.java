package ClavarChat.Models.ClvcEvent;

import ClavarChat.Models.ClvcEvent.ClvcEvent;

public class RemoveUserEvent extends ClvcEvent
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
