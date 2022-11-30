package BDD.Connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {
    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:/Users/clementroussel/Desktop/Programmation/ProjetJava/Implementation/src/main/resources/BDD/Connect/ClavarDataBase.db";

        // SQL statement for creating a new table
        String sql1 = "CREATE TABLE IF NOT EXISTS users (\n"
                + " id_user integer PRIMARY KEY,\n"
                + " pseudonym text NOT NULL\n"
                + ");";
        String sql2 = "CREATE TABLE IF NOT EXISTS conversations (\n"
                + " id_conv integer PRIMARY KEY,\n"
                + " id_user1 integer NOT NULL,\n"
                + " id_user2 integer NOT NULL\n"
                + ");";
        String sql3 = "CREATE TABLE IF NOT EXISTS messages (\n"
                + " id_msg integer PRIMARY KEY,\n"
                + " id_conv integer NOT NULL,\n"
                + " id_user integer NOT NULL,\n"
                + " pseudonym text NOT NULL,\n"
                + " date text NOT NULL\n"
                + ");";

        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql1);
            stmt.execute(sql2);
            stmt.execute(sql3);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        createNewTable();
    }


}
