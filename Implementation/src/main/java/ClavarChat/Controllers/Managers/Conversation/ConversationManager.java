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

    public ArrayList<Message> getConversation(String pseudo)
    {
        if (!this.conversations.containsKey(pseudo))
        {
            Log.Warning(this.getClass().getName() + " No conversation with : " + pseudo);
            return null;
        }

        return this.conversations.get(pseudo);
    }

    public boolean conversationExist(String pseudo)
    {
        return this.conversations.containsKey(pseudo);
    }

    public void createConversation(String pseudo)
    {
        if (!this.conversationExist(pseudo))
        {
            Log.Info(this.getClass().getName() + " Creating conversation with : " + pseudo);
            this.conversations.put(pseudo, new ArrayList<>());
        }
        else
        {
            Log.Warning(this.getClass().getName() + " Conversation already exist with : " + pseudo);
        }
    }

    public void addMessage(String pseudo, String from, String text)
    {
        if (this.conversationExist(pseudo))
        {
            Message message = new Message(from, text);
            this.conversations.get(pseudo).add(message);
        }
        else
        {
            Log.Warning(this.getClass().getName() + " No conversation with : " + pseudo);
        }
    }
}
