package BDD.Connect;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SelectConversation {

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


    public void selectAll(){
        String sql = "SELECT * FROM conversations";

        try {
            Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            System.out.println("id_conv"+  "\t" +"id_user1"+  "\t" +"id_user2");
            while (rs.next()) {
                System.out.println(rs.getInt("id_conv") +  "\t" +
                        rs.getInt("id_user1") + "\t" +
                        rs.getInt("id_user2"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SelectConversation app = new SelectConversation();
        app.selectAll();
    }

}