package ClavarChat.Models.Events;

public class Event
{
    public static final String ANY = "ANY";
    public String type;

    public Event(String type)
    {
        this.type = type;
    }
}