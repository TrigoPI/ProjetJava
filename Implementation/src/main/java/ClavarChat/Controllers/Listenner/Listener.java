package ClavarChat.Controllers.Listenner;

import ClavarChat.Models.Events.Event;

public interface Listener
{
    void onEvent(Event event);
}
