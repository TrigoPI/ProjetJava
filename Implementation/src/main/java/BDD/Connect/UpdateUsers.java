package BDD.Connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateUsers {

    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:/Users/clementroussel/Desktop/Programmation/ProjetJava/Implementation/src/main/resources/BDD/Connect/ClavarDataBase.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void update(int id_user, String pseudo) {
        String sql = "UPDATE users SET pseudonym = ? "
                + "WHERE id_user = ?";

        try {
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            // set the corresponding param
            pstmt.setString(1, pseudo);
            pstmt.setInt(2, id_user);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        UpdateUsers app = new UpdateUsers();
        // update the warehouse with id 3
        app.update(6969, "ClémentTest");
    }

}
