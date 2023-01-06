package ClavarChat.Controllers.Handlers;

import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcMessage.ClvcMessage;
import ClavarChat.Models.ClvcMessage.TextMessage;

public class MessageHandler implements MessageListener
{
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
    }
}
