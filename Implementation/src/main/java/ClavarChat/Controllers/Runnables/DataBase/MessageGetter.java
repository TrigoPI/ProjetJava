package ClavarChat.Controllers.Runnables.DataBase;

import ClavarChat.Controllers.Handlers.MessageHandler;
import ClavarChat.Controllers.Managers.Thread.TMRunnable;

public class MessageGetter implements TMRunnable
{
    private final MessageHandler messageHandler;
    private final int conversationId;
    private final int startId;
    private final int n;

    public MessageGetter(MessageHandler messageHandler, int conversationId)
    {
        this.messageHandler = messageHandler;
        this.conversationId = conversationId;
        this.startId = -1;
        this.n = -1;
    }

    public MessageGetter(MessageHandler messageHandler, int conversationId, int startId, int n)
    {
        this.messageHandler = messageHandler;
        this.conversationId = conversationId;
        this.startId = startId;
        this.n = n;
    }

    @Override
    public void run()
    {
        if (this.startId == -1)
        {
            this.messageHandler.asynchronousGetMessage(this.conversationId);
        }
        else
        {
            this.messageHandler.asynchronousGetMessage(this.conversationId, this.startId, this.n);
        }

    }
}
