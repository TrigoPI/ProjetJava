package ClavarChat.Models.Events.Thread;

import ClavarChat.Models.Events.Event;

public class ThreadEvent extends Event
{
    public static final String THREAD_FINISHED = "THREAD_FINISHED";

    public int id;

    public ThreadEvent(String event, int id)
    {
        super(event);
        this.id = id;
    }
}
