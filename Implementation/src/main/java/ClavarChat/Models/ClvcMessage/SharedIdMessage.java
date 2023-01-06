package ClavarChat.Models.ClvcMessage;

public class SharedIdMessage extends ClvcMessage
{
    public static final String SHARED_ID = "SHARED_ID";

    public final String sharedId;
    public final String pseudo;

    public SharedIdMessage(String sharedId, int id, String pseudo)
    {
        super(SHARED_ID);
        this.id = id;
        this.pseudo = pseudo;
        this.sharedId = sharedId;
    }
    public final int id;
}
