package ClavarChat.Models.Events.Login;

import ClavarChat.Models.Events.Event;

public class NewUserEvent extends Event
{
    public static final String NEW_USER = "NEW_USER";

    public String pseudo;
    public String id;

    public NewUserEvent(String pseudo, String id)
    {
        super(NEW_USER);

        this.pseudo = pseudo;
        this.id = id;
    }
}
