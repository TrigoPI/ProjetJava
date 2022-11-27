package BDD.Connect;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertMessage {
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


    public void InsertMessage(int id_user, int id_conv, String text, String date) {
        String sql = "INSERT INTO message(id_user,id_conv,text,date) VALUES(?,?,?,?)";

        try {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id_user);
            pstmt.setInt(2, id_conv);
            pstmt.setString(3,text);
            pstmt.setString(4,date );
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        InsertMessage app = new InsertMessage();
    }
}
