package BDD.Connect;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertUser {
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


    public void InsertUser(String pseudo) {
        String sql = "INSERT INTO users(pseudo) VALUES(?)";

        try {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,pseudo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        InsertUser app = new InsertUser();
        // insert three new rows
        app.InsertUser("Alexis");
        app.InsertUser("Clement");
    }
}
