package ClavarChat.Models.ClvcEvent;

import ClavarChat.Models.ClvcEvent.ClvcEvent;

public class LoginEvent extends ClvcEvent
{
    public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
    public static final String LOGIN_FAILED  = "LOGIN_FAILED";

    public LoginEvent(String event)
    {
        super(event);
    }
}
