package ClavarChat.Models.ClvcEvent;

public class NewUserEvent extends ClvcEvent
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
