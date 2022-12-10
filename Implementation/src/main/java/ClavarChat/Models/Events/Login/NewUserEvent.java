package ClavarChat.Models.Events.Login;

import ClavarChat.Models.Events.Event;

public class NewUserEvent extends Event
{
    public static final String NEW_USER = "NEW_USER";

    public String pseudo;

    public NewUserEvent(String pseudo)
    {
        super(NEW_USER);
        this.pseudo = pseudo;
    }
}
