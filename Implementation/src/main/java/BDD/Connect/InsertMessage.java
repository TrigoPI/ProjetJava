package BDD.Connect;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertMessage {
    private Connection connect() {
        // SQLite connection string
        String url ="jdbc:sqlite:/Users/clementroussel/Desktop/Programmation/ProjetJava/Implementation/src/main/resources/BDD/Connect/ClavarDataBase.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    public void InsertMessage(int id_msg,int id_conv, int id_user, String msg, String date) {
        String sql = "INSERT INTO messages(id_msg,id_conv,id_user,msg,date) VALUES(?,?,?,?,?)";

        try {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id_msg);
            pstmt.setInt(2, id_conv);
            pstmt.setInt(3, id_user);
            pstmt.setString(4,msg);
            pstmt.setString(5,date);
            pstmt.executeUpdate();
            System.out.println(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        InsertMessage app = new InsertMessage();
        app.InsertMessage(1,1,6969,"salut","30/11/2022");
    }
}
