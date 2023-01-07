package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.API.EventAPI;
import ClavarChat.Models.ClvcEvent.Message.MessageEvent;
import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcMessage.ClvcMessage;
import ClavarChat.Models.ClvcMessage.TextMessage;

public class MessageHandler implements MessageListener
{
    private final EventAPI eventAPI;
    private final DataBaseAPI dataBaseAPI;

    public MessageHandler(EventAPI eventAPI, DataBaseAPI dataBaseAPI)
    {
        this.eventAPI = eventAPI;
        this.dataBaseAPI = dataBaseAPI;
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
        System.out.println("New Message from : [ " + data.pseudo + " / " + data.sharedId + " ] --> " + data.message);
        int conversationId = this.dataBaseAPI.getConversationId(data.sharedId);
        this.dataBaseAPI.addMessage(conversationId, data.id, data.message);
        this.eventAPI.notify(new MessageEvent(data.sharedId, data.pseudo, data.id, data.message));
    }
}
