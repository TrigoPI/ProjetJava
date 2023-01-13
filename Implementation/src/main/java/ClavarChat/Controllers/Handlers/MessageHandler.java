package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.API.EventAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.ClvcEvent.MessageEvent;
import ClavarChat.Models.ClvcEvent.TypingEvent;
import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcNetworkMessage.ClvcNetworkMessage;
import ClavarChat.Models.ClvcNetworkMessage.TextMessage;
import ClavarChat.Models.ClvcNetworkMessage.TypingMessage;
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
    public void onData(String srcIp, ClvcNetworkMessage message)
    {
        switch (message.type)
        {
            case TextMessage.TEXT_MESSAGE -> this.onTextMessage((TextMessage)message);
            case TypingMessage.TYPING_START -> this.onTypingStart((TypingMessage)message);
            case TypingMessage.TYPING_END -> this.onTypingEnd((TypingMessage)message);
        }
    }

    private void onTypingEnd(TypingMessage message)
    {
        this.eventAPI.notify(new TypingEvent(message.userId, message.sharedId, false));
    }

    private void onTypingStart(TypingMessage message)
    {
        this.eventAPI.notify(new TypingEvent(message.userId, message.sharedId, true));
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
