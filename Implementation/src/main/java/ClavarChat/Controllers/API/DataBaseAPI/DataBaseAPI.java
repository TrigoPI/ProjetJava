package ClavarChat.Controllers.API.DataBaseAPI;

import ClavarChat.Controllers.Managers.DataBase.DataBaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseAPI {

    private DataBaseManager manager = new DataBaseManager();

    public void insertConv(String id_user)
    {
        String values = "VALUES(" + id_user +")";
        manager.insert("conversations(id_user)",values);
    }

    public void insertMsg(String id_conv, String id_user, String msg, String date)
    {
        String values = "VALUES(" + id_conv + "," + id_user + "," + "'" + msg + "'" + "," + "'" + date + "'" +")";
        manager.insert("messages(id_conv,id_user,msg,date)",values);
    }

    public void insertUser(String id_user, String pseudonym, String img)
    {
        String values = "VALUES(" + id_user + "," + "'" + pseudonym + "'" + "," + "'" + img + "'" + ")";
        manager.insert("users(id_user,pseudonym,img)", values);
    }

    public void updatePseudonym(String id_user, String newPseudonym)
    {
        String update = "'" + newPseudonym +"'";
        manager.update("users", "pseudonym", "id_user", id_user,update);
    }

    public void updateImg(String id_user, String newIMG)
    {
        String update = "'" + newIMG +"'";
        manager.update("users", "img", "id_user", id_user,update);
    }

    public ResultSet selectMessage(String id_conv)
    {
        return manager.select("id_user,msg,date", "messages", " WHERE id_conv = " + id_conv);
    }

    public String searchIdConv (String id_user) throws SQLException {
        ResultSet rs = manager.select("id_conv", "conversations", " WHERE id_user = " + id_user);
        return rs.getString("id_conv");
    }

}
