package ClavarChat.Controllers.DataBaseAPI;

import ClavarChat.Controllers.Managers.DataBaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseAPI {

    private DataBaseManager manager = new DataBaseManager();

    public void insertConv(String id_user1, String id_user2)
    {
        String values = "VALUES(" + id_user1 + "," + id_user2 +")";
        manager.insert("conversations",values);
    }

    public void insertMsg(String id_msg, String id_conv, String id_user, String msg, String date)
    {
        String values = "VALUES(" + id_msg + "," + id_conv + "," + id_user + "," + msg + "," + date +")";
        manager.insert("messages",values);
    }

    public void insertUser(String id_user, String pseudonym)
    {
        String values = "VALUES(" + id_user + "," + pseudonym + ")";
        manager.insert("users", values);
    }

    public void updatePseudonym(String id_user, String newPseudonym)
    {
        manager.update("users", "pseudonym", "id_user",id_user,newPseudonym);
    }

    public ResultSet selectMessage(String id_conv)
    {
        return manager.select("id_user,msg,date", "msg", " WHERE id_conv = " + id_conv);
    }

    public String searchIdConv (String id_user1, String id_user2) throws SQLException {
        ResultSet rs = manager.select("id_conv", "conversations", " WHERE id_user1 = " + id_user1 + " AND id_user2 = " + id_user2);
        return rs.getString("id_conv");
    }
}
