package ClavarChat.Models.Events;

public class LoginEvent extends Event
{
    public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
    public static final String LOGIN_FAILED  = "LOGIN_FAILED";

    public LoginEvent(String event)
    {
        super(event);
    }
}
