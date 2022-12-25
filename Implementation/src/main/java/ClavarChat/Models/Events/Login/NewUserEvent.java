package ClavarChat.Models.Events.Login;

import ClavarChat.Models.Events.Event;

public class NewUserEvent extends Event
{
    public static final String NEW_USER = "NEW_USER";

    public int userId;
    public String pseudo;

    public NewUserEvent(int userId, String pseudo)
    {
        super(NEW_USER);
        this.userId = userId;
        this.pseudo = pseudo;
    }
}
