package ClavarChat.Controllers.Managers.DataBase;

import java.sql.*;

public class DataBaseManager
{
    private String path;
    private String url;
    private Connection conn;


    public DataBaseManager()
    {
        this.path = "/Users/clementroussel/Desktop/Programmation/ProjetJava/Implementation/src/main/resources/BDD/ClavarDataBase.db";
        this.url="jdbc:sqlite:" + path;
        this.connect();
    }
    public void connect()
    {
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // POUR LES STRING, IL FAUT LES METTRE ENTRE QUOTE ''
    public void createTable(String name, String fields)
    {
        String sql = "CREATE TABLE IF NOT EXISTS " + name + " (" + fields + " )";
        try{
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /* request create
    "CREATE TABLE IF NOT EXISTS test (
        test1 integer PRIMARY KEY,
        test2 text NOT NULL
        )
    */

    public void insert(String name_arguments, String values)
    {
        String sql = "INSERT INTO " + name_arguments + " " + values;
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /* request insert
    INSERT INTO messages(id_msg,id_conv,id_user,msg,date) VALUES(?,?,?,?,?)
     */

    public void update(String name, String field_value, String name_primary_key, String primary_key_value, String new_value)
    {
        String sql = "UPDATE " + name + " SET " + field_value + " = " + new_value + " WHERE " + name_primary_key + " = " + primary_key_value;
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /* request update
        UPDATE users SET pseudonym = ?
        WHERE id_user = ?
     */

    public void delete(String name, String id, String id_value)
    {
        String sql = "DELETE FROM " + name + " WHERE " + id + " = " + id_value;
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /* request delete
    DELETE FROM warehouses WHERE id = ?
     */

    public ResultSet select(String field, String name, String filter)
    {
        String sql = "SELECT " + field + " FROM " + name + filter;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ResultSet rs = null;
            return rs;
        }
    }
    /* request select
    select id_conv FROM conversation
     */

    //public static void main(String[] args)

}
