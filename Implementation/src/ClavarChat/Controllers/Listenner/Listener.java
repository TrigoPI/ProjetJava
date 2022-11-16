package ClavarChat.Controllers.Listenner;

import ClavarChat.Models.Events.Event;

public interface Listener
{
    public void onEvent(Event event);
}
