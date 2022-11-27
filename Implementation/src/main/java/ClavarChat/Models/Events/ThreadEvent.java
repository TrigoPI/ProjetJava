package ClavarChat.Models.Events;

public class ThreadEvent extends Event
{
    public enum THREAD_STATUS { FINISHED }

    public int id;
    public THREAD_STATUS status;

    public ThreadEvent(THREAD_STATUS status, int id)
    {
        super(EVENT_TYPE.EVENT_THREAD);

        this.id = id;
        this.status = status;
    }
}
