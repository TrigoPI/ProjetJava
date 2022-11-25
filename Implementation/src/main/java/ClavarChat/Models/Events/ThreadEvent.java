package ClavarChat.Models.Events;

public class ThreadEvent extends Event
{
    public enum THREAD_STATUS { FINISHED }

    public String threadID;
    public THREAD_STATUS status;

    public ThreadEvent(THREAD_STATUS status, String threadID)
    {
        super(EVENT_TYPE.EVENT_THREAD);

        this.threadID = threadID;
        this.status = status;
    }
}
