package ClavarChat.Controllers.Managers.Conversation;

import ClavarChat.Models.Message.Message;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class ConversationManager
{
    private final HashMap<String, ArrayList<Message>> conversations;

    public ConversationManager()
    {
        this.conversations = new HashMap<>();
    }

    public ArrayList<Message> getConversation(String conversationName)
    {
        if (!this.conversations.containsKey(conversationName))
        {
            Log.Warning(this.getClass().getName() + " No conversation with namae: " + conversationName);
            return null;
        }

        return this.conversations.get(conversationName);
    }

    public boolean conversationExist(String pseudo)
    {
        return this.conversations.containsKey(pseudo);
    }

    public void createConversation(String conversationName)
    {
        if (!this.conversationExist(conversationName))
        {
            Log.Info(this.getClass().getName() + " Creating conversation with : " + conversationName);
            this.conversations.put(conversationName, new ArrayList<>());
        }
        else
        {
            Log.Warning(this.getClass().getName() + " Conversation already exist with : " + conversationName);
        }
    }

    public void addMessage(String conversationName, String src, String dst, String text)
    {
        if (this.conversationExist(conversationName))
        {
            Message message = new Message(src, dst, text);
            this.conversations.get(conversationName).add(message);
        }
        else
        {
            Log.Warning(this.getClass().getName() + " No conversation with : " + dst);
        }
    }
}
