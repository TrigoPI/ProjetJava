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
import ClavarChat.Models.Message.Message;
import ClavarChat.Utils.Log.Log;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler implements MessageListener
{
    private final LinkedBlockingQueue<Message> buffer;
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

    public Message getLastMessage()
    {
        return this.buffer.poll();
    }

    public void asynchronousGetMessage(int conversationId, int fromMessageId, int n)
    {
        for (int messageId : this.dataBaseAPI.getMessagesId(conversationId, fromMessageId, n))
        {
            int userId = this.dataBaseAPI.getMessageUserId(messageId);
            String message = this.dataBaseAPI.getMessageText(messageId);
            String sharedId = this.dataBaseAPI.getSharedIdFromConversationId(conversationId);
            this.buffer.add(new Message(userId, messageId, sharedId, message));
        }

        this.eventAPI.notify(new MessageEvent());
    }


    public void asynchronousGetMessage(int conversationId)
    {
        for (int messageId : this.dataBaseAPI.getMessagesId(conversationId))
        {
            int userId = this.dataBaseAPI.getMessageUserId(messageId);
            String message = this.dataBaseAPI.getMessageText(messageId);
            String sharedId = this.dataBaseAPI.getSharedIdFromConversationId(conversationId);
            this.buffer.add(new Message(userId, messageId, sharedId, message));
        }

        this.eventAPI.notify(new MessageEvent());
    }

    @Override
    public void onData(String srcIp, ClvcNetworkMessage message)
    {
        switch (message.type)
        {
            case TextMessage.TEXT_NETWORK_MESSAGE :
                this.onTextNetworkMessage((TextMessage)message);
                break;
            case TypingMessage.TYPING_START :
                this.onTypingStart((TypingMessage)message);
                break;
            case TypingMessage.TYPING_END :
                this.onTypingEnd((TypingMessage)message);
                break;
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

    private void onTextNetworkMessage(TextMessage data)
    {
        Log.Info("New Message from : [ " + data.userId + " / " + data.sharedId + " ] --> " + data.message);

        int conversationId = this.dataBaseAPI.getConversationId(data.sharedId);
        int messageId = this.dataBaseAPI.addMessage(conversationId, data.userId, this.userManager.getId(), data.message);

        this.buffer.add(new Message(data.userId, messageId, data.sharedId, data.message));
        this.eventAPI.notify(new MessageEvent());
    }
}
