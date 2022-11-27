package BDD.Connect;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertConversation {
    private Connection connect() {
        // SQLite connection string
        URL path = Connect.class.getResource("ClavarDataBase.db");
        String url = "jdbc:sqlite:" + path.getPath();
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    public void InsertConversation(int id_user1, int id_user2) {
        String sql = "INSERT INTO conversation(id_user1,id_user2) VALUES(?,?)";

        try {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id_user1);
            pstmt.setInt(2, id_user2);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        InsertConversation app = new InsertConversation();
    }
}
