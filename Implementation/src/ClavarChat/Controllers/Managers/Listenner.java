package ClavarChat.Controllers.Managers;

import ClavarChat.Models.Events.Event;

public interface Listenner
{
    public void onEvent(Event event);
}
