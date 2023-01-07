package ClavarChat.Controllers.API;

import ClavarChat.Controllers.Managers.DataBase.DataBaseManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.Path.Path;

import java.util.ArrayList;
import java.util.UUID;

public class DataBaseAPI
{
    private final DataBaseManager dataBaseManager;
    private final UserManager userManager;

    public DataBaseAPI(UserManager userManager)
    {
        this.dataBaseManager = new DataBaseManager(Path.getWorkingPath() + "/src/main/resources/BDD/ClavarDataBase.db");
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

    public String getConversationSharedId(int conversationId)
    {
        int resultId = this.dataBaseManager.executeQuery("SELECT shared_id FROM Conversation WHERE conversation_id = %d", conversationId);

        if (resultId == -1)
        {
            Log.Error(this.getClass().getName() + " ERROR getting conversation with id : " + conversationId);
            return null;
        }

        ArrayList<String> sharedIds = this.dataBaseManager.decodeAsString(resultId, 1);
        this.dataBaseManager.removeResultSet(resultId);

        return sharedIds.get(0);
    }

    public int getConversationId(String sharedId)
    {
        int resultId = this.dataBaseManager.executeQuery("SELECT conversation_id FROM Conversation WHERE shared_id='%s'", sharedId);

        if (resultId == -1)
        {
            Log.Error(this.getClass().getName() + " ERROR getting conversation with shared_id : " + sharedId);
            return -1;
        }

        ArrayList<Integer> conversationId = this.dataBaseManager.decodeAsInt(resultId, 1);
        this.dataBaseManager.removeResultSet(resultId);
        return conversationId.get(0);
    }

    public int getMessageUserId(int messageId)
    {
        int resultId = this.dataBaseManager.executeQuery("SELECT user_id FROM Message WHERE message_id='%d'", messageId);

        if (resultId == -1)
        {
            Log.Error(this.getClass().getName() + " ERROR getting message with id : " + messageId);
            return -1;
        }

        ArrayList<Integer> messages = this.dataBaseManager.decodeAsInt(resultId, 1);
        this.dataBaseManager.removeResultSet(resultId);
        return messages.get(0);
    }

    public int createConversation(String conversationName, int userId)
    {
        if (this.userManager.isLogged())
        {
            Log.Error(this.getClass().getName() + " Cannot create conversation user is not logged");
            return -1;
        }

        String sharedId = UUID.randomUUID().toString();
        int preparedStatementId = this.dataBaseManager.createPreparedStatement("INSERT INTO Conversation(conversation_name, shared_id) VALUES(?, ?)");
        this.dataBaseManager.setString(preparedStatementId, 1, conversationName);
        this.dataBaseManager.setString(preparedStatementId, 2, sharedId);
        this.dataBaseManager.executePreparedStatement(preparedStatementId);

        int conversationId = this.dataBaseManager.getIdGenerated(preparedStatementId);
        if (conversationId == -1) return -1;

        this.dataBaseManager.execute("INSERT INTO Read(user_id, conversation_id) VALUES(%d, %d)", this.userManager.getId(), conversationId);
        this.dataBaseManager.execute("INSERT INTO Read(user_id, conversation_id) VALUES(%d, %d)", userId, conversationId);

        this.dataBaseManager.removePreparedStatement(preparedStatementId);

        return conversationId;
    }

    public int createConversation(String conversationName, String sharedId, int userId)
    {
        if (this.userManager.isLogged())
        {
            Log.Error(this.getClass().getName() + " Cannot create conversation user is not logged");
            return -1;
        }

        int preparedStatementId = this.dataBaseManager.createPreparedStatement("INSERT INTO Conversation(conversation_name, shared_id) VALUES(?, ?)");
        this.dataBaseManager.setString(preparedStatementId, 1, conversationName);
        this.dataBaseManager.setString(preparedStatementId, 2, sharedId);
        this.dataBaseManager.executePreparedStatement(preparedStatementId);

        int conversationId = this.dataBaseManager.getIdGenerated(preparedStatementId);
        if (conversationId == -1) return -1;

        this.dataBaseManager.execute("INSERT INTO Read(user_id, conversation_id) VALUES(%d, %d)", this.userManager.getId(), conversationId);
        this.dataBaseManager.execute("INSERT INTO Read(user_id, conversation_id) VALUES(%d, %d)", userId, conversationId);

        this.dataBaseManager.removePreparedStatement(preparedStatementId);

        return conversationId;
    }

    public void addUser(int userId, String pseudo, byte[] avatar)
    {
        if (this.userExist(userId))
        {
            this.updateUser(userId, pseudo, avatar);
            return;
        }

        int preparedStatementId = this.dataBaseManager.createPreparedStatement("INSERT OR IGNORE INTO User(user_id, pseudo, avatar) VALUES(?, ?, ?)");
        this.dataBaseManager.setInt(preparedStatementId, 1, userId);
        this.dataBaseManager.setString(preparedStatementId, 2, pseudo);
        this.dataBaseManager.setBytes(preparedStatementId, 3, avatar);
        this.dataBaseManager.executePreparedStatement(preparedStatementId);
        this.dataBaseManager.removePreparedStatement(preparedStatementId);
    }

    public void addMessage(int conversationId, int userId, String message)
    {
        this.dataBaseManager.execute("INSERT INTO Message(date, text, conversation_id, user_id) VALUES('ok', '%s', '%d', '%d')", message, conversationId, userId);
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
