package ClavarChat.Controllers.Chain;

import ClavarChat.Models.ChainData.Request.Request;

public abstract class Handler
{
    protected Handler next;

    public void setNext(Handler next)
    {
        this.next = next;
    }

    public abstract String handle(Request request);

}
