package BDD.Connect;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateNewTable {
    /**
     * Create a new table in the test database
     *
     */
    public static void createNewTable() {
        // SQLite connection string
        URL path = CreateNewTable.class.getResource("ClavarDataBase.db");
        String url = "jdbc:sqlite:" + path.getPath();

        // SQL statement for creating a new table
        String sql1 = "CREATE TABLE IF NOT EXISTS users (\n"
                + " id_user integer PRIMARY KEY,\n"
                + "	pseudo text NOT NULL\n"
                + ");";

        String sql2 = "CREATE TABLE IF NOT EXISTS conversation (\n"
                + "	id_conv integer PRIMARY KEY,\n"
                + "	id_user1 integer NOT NULL,\n"
                + "	id_user2 integer NOT NULL\n"
                + ");";

        String sql3 = "CREATE TABLE IF NOT EXISTS message (\n"
                + "	id_msg integer PRIMARY KEY,\n"
                + "	id_user integer NOT NULL,\n"
                + "	id_conv integer NOT NULL,\n"
                + " msg text NOT NULL,\n"
                + " date text NOT NULL\n"
                + ");";


        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            // create a new table
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
