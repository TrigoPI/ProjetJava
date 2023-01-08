package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.API.EventAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.ClvcEvent.MessageEvent;
import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcMessage.ClvcMessage;
import ClavarChat.Models.ClvcMessage.TextMessage;
import ClavarChat.Utils.Log.Log;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler implements MessageListener
{
    private final LinkedBlockingQueue<TextMessage> buffer;
    private final EventAPI eventAPI;
    private final DataBaseAPI dataBaseAPI;
    private final UserManager userManager;

    public MessageHandler(EventAPI eventAPI, DataBaseAPI dataBaseAPI, UserManager userManager)
    {
        this.buffer = new LinkedBlockingQueue<>();

        this.eventAPI = eventAPI;
        this.dataBaseAPI = dataBaseAPI;
        this.userManager = userManager;
    }

    public boolean hasMessage()
    {
        return !this.buffer.isEmpty();
    }

    public TextMessage getLastMessage()
    {
        return this.buffer.poll();
    }

    @Override
    public void onData(String srcIp, ClvcMessage message)
    {
        switch (message.type)
        {
            case TextMessage.TEXT_MESSAGE -> this.onTextMessage((TextMessage)message);
        }
    }

    private void onTextMessage(TextMessage data)
    {
        Log.Info("New Message from : [ " + data.pseudo + " / " + data.sharedId + " ] --> " + data.message);
        int conversationId = this.dataBaseAPI.getConversationId(data.sharedId);

        this.buffer.add(data);
        this.dataBaseAPI.addMessage(conversationId, data.id, this.userManager.getId(), data.message);
        this.eventAPI.notify(new MessageEvent());
    }
}
