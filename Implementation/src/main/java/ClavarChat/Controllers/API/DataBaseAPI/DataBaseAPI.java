package ClavarChat.Controllers.API.DataBaseAPI;

import ClavarChat.Controllers.Managers.DataBase.DataBaseManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.Path.Path;

import java.util.ArrayList;

public class DataBaseAPI
{
    private final DataBaseManager dataBaseManager;
    private final UserManager userManager;

    public DataBaseAPI(UserManager userManager)
    {
        this.dataBaseManager = new DataBaseManager(Path.getWorkingPath() + "/src/main/resources/BDD/Connect/ClavarDataBase.db");
        this.userManager = userManager;
    }

    public boolean userExist(int id)
    {
        int resultId = this.dataBaseManager.executeQuery("SELECT user_id FROM User WHERE user_id = %d", id);
        boolean isEmpty = !this.dataBaseManager.decodeAsInt(resultId, 1).isEmpty();
        this.dataBaseManager.removeResultSet(resultId);
        return isEmpty;
    }

    public String getUserPseudo(int userId)
    {
        if (!this.userExist(userId))
        {
            Log.Error(this.getClass().getName() + " No user with id : " + userId);
            return null;
        }

        int resultId = this.dataBaseManager.executeQuery("SELECT pseudo FROM User WHERE user_id = %d", userId);
        ArrayList<String> pseudos = this.dataBaseManager.decodeAsString(resultId, 1);
        this.dataBaseManager.removeResultSet(resultId);
        return pseudos.get(0);
    }

    public byte[] getUserAvatar(int userId)
    {
        if (!this.userExist(userId))
        {
            Log.Error(this.getClass().getName() + " No user with id : " + userId);
            return null;
        }

        int resultId = this.dataBaseManager.executeQuery("SELECT avatar FROM User WHERE user_id = %d", userId);
        ArrayList<byte[]> avatars = this.dataBaseManager.decodeAsBytes(resultId, 1);
        byte[] buffer = avatars.get(0);
        this.dataBaseManager.removeResultSet(resultId);
        return buffer;
    }

    public ArrayList<Integer> getUsersId()
    {
        int id = this.userManager.getId();
        int resultId = this.dataBaseManager.executeQuery("SELECT user_id FROM User WHERE user_id != %d", id);
        ArrayList<Integer> users = this.dataBaseManager.decodeAsInt(resultId, 1);
        this.dataBaseManager.removeResultSet(resultId);
        return users;
    }

    public ArrayList<Integer> getConversationWith(int userId)
    {
        int id = this.userManager.getId();
        int resultId = this.dataBaseManager.executeQuery("SELECT conversation_id FROM Read WHERE user_id = %d OR user_id = %d GROUP BY conversation_id HAVING COUNT(conversation_id) > 1", id, userId);
        ArrayList<Integer> conversationsId = this.dataBaseManager.decodeAsInt(resultId, 1);
        this.dataBaseManager.removeResultSet(resultId);
        return conversationsId;
    }

    public ArrayList<Integer> getConversationsId()
    {
        int id = this.userManager.getId();
        int resultId = this.dataBaseManager.executeQuery("SELECT conversation_id FROM Read WHERE user_id = %d", id);
        ArrayList<Integer> conversationsId = this.dataBaseManager.decodeAsInt(resultId, 1);

        this.dataBaseManager.removeResultSet(resultId);

        return conversationsId;
    }

    public ArrayList<Integer> getMessagesId(int conversationId)
    {
        int resultId = this.dataBaseManager.executeQuery("SELECT message_id FROM Message WHERE conversation_id = %d", conversationId);

        if (resultId == -1)
        {
            Log.Error(this.getClass().getName() + " ERROR getting messages in conversation : " + conversationId);
            return null;
        }

        ArrayList<Integer> messagesId = this.dataBaseManager.decodeAsInt(resultId, 1);
        this.dataBaseManager.removeResultSet(resultId);
        return messagesId;
    }

    public ArrayList<Integer> getUsersInConversation(int conversationId)
    {
        int userId = this.userManager.getId();
        int resultId = this.dataBaseManager.executeQuery("SELECT user_id FROM Read WHERE conversation_id = %d AND user_id != %d", conversationId, userId);

        if (resultId == -1)
        {
            Log.Error(this.getClass().getName() + " ERROR getting users in conversation : " + conversationId);
            return null;
        }

        ArrayList<Integer> users = this.dataBaseManager.decodeAsInt(resultId, 1);
        this.dataBaseManager.removeResultSet(resultId);
        return users;
    }

    public String getMessageText(int messageId)
    {
        int resultId = this.dataBaseManager.executeQuery("SELECT text FROM Message WHERE message_id = %d", messageId);

        if (resultId == -1)
        {
            Log.Error(this.getClass().getName() + " ERROR getting message with id : " + messageId);
            return null;
        }

        ArrayList<String> messages = this.dataBaseManager.decodeAsString(resultId, 1);
        this.dataBaseManager.removeResultSet(resultId);
        return messages.get(0);
    }

    public int getMessageUserId(int messageId)
    {
        int resultId = this.dataBaseManager.executeQuery("SELECT user_id FROM Message WHERE message_id = %d", messageId);

        if (resultId == -1)
        {
            Log.Error(this.getClass().getName() + " ERROR getting message with id : " + messageId);
            return -1;
        }

        ArrayList<Integer> messages = this.dataBaseManager.decodeAsInt(resultId, 1);
        this.dataBaseManager.removeResultSet(resultId);
        return messages.get(0);
    }

    public void addUser(int userId, String pseudo, byte[] avatar)
    {
        if (this.userExist(userId))
        {
            this.updateUser(userId, pseudo, avatar);
        }
        else
        {
            int preparedStatementId = this.dataBaseManager.createPreparedStatement("INSERT OR IGNORE INTO User(user_id, pseudo, avatar) VALUES(?, ?, ?)");
            this.dataBaseManager.setInt(preparedStatementId, 1, userId);
            this.dataBaseManager.setString(preparedStatementId, 2, pseudo);
            this.dataBaseManager.setBytes(preparedStatementId, 3, avatar);
            this.dataBaseManager.executePreparedStatement(preparedStatementId);
            this.dataBaseManager.removePreparedStatement(preparedStatementId);
        }
    }

    public void addMessage(int conversationId, int userId, String message)
    {
        this.dataBaseManager.execute("INSERT INTO Message(date, text, conversation_id, user_id) VALUES('ok', '%s', '%d', '%d')", message, conversationId, userId);
    }

    public void createConversation(String conversationName, int userId1, int userId2)
    {
        int preparedStatementId = this.dataBaseManager.createPreparedStatement("INSERT INTO Conversation(conversation_name) VALUES(?)");
        this.dataBaseManager.setString(preparedStatementId, 1, conversationName);
        this.dataBaseManager.executePreparedStatement(preparedStatementId);

        int conversationId = this.dataBaseManager.getIdGenerated(preparedStatementId);
        if (conversationId == -1) return;

        this.dataBaseManager.execute("INSERT INTO Read(user_id, conversation_id) VALUES(%d, %d)", userId1, conversationId);
        this.dataBaseManager.execute("INSERT INTO Read(user_id, conversation_id) VALUES(%d, %d)", userId2, conversationId);

        this.dataBaseManager.removePreparedStatement(preparedStatementId);
    }

    private void updateUser(int userId, String pseudo, byte[] avatar)
    {
        int statementId = this.dataBaseManager.createPreparedStatement("UPDATE User SET pseudo = ?, avatar = ? WHERE user_id = ?");
        this.dataBaseManager.setString(statementId, 1, pseudo);
        this.dataBaseManager.setBytes(statementId, 2, avatar);
        this.dataBaseManager.setInt(statementId, 3, userId);
        this.dataBaseManager.executePreparedStatement(statementId);
        this.dataBaseManager.removePreparedStatement(statementId);
    }

    public void clear()
    {
        this.dataBaseManager.execute("DELETE FROM User");
        this.dataBaseManager.execute("DELETE FROM Conversation");
        this.dataBaseManager.execute("DELETE FROM Read");
        this.dataBaseManager.execute("DELETE FROM Message");
    }
}
