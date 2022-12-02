package ClavarChat.Controllers.Modules;

public abstract class Handler
{
    protected Handler next;

    public void setNext(Handler next)
    {
        this.next = next;
    }

    public void handle()
    {
        if (this.next != null) this.next.handle();
    }
}
