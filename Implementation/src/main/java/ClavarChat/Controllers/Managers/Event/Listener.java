package ClavarChat.Controllers.Managers.Event;

import ClavarChat.Models.Events.Event;

public interface Listener
{
    void onEvent(Event event);
}
