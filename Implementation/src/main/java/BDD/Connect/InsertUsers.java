package BDD.Connect;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertUsers {
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


    public void InsertUsers(int id_user, String pseudo) {
        String sql = "INSERT INTO users(id_user,pseudonym) VALUES(?,?)";

        try {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id_user);
            pstmt.setString(2, pseudo);
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

        InsertUsers app = new InsertUsers();
        app.InsertUsers(6969,"Clément");
        app.InsertUsers(8080,"Alexis");
    }
}
