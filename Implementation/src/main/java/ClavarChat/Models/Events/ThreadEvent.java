package ClavarChat.Models.Events;

import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Enums.THREAD_EVENT_TYPE;

public class ThreadEvent extends Event
{
    public THREAD_EVENT_TYPE threadEventType;
    public String threadID;

    public ThreadEvent(THREAD_EVENT_TYPE threadEventType, String threadID)
    {
        super(EVENT_TYPE.THREAD_EVENT);
        this.threadEventType = threadEventType;
        this.threadID = threadID;
    }
}
