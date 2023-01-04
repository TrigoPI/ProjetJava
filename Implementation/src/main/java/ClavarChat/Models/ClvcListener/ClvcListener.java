package ClavarChat.Models.ClvcListener;

import ClavarChat.Models.ClvcEvent.ClvcEvent;

public interface ClvcListener
{
    void onEvent(ClvcEvent event);
}
