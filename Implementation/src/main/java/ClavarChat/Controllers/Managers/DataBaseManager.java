package ClavarChat.Controllers.Managers;



import java.net.URL;
import java.sql.*;

public class DataBaseManager
{
    private String path;
    private String url;
    private Connection conn;


    public DataBaseManager()
    {
        this.path = "./src/main/resources/BDD/Connect/ClavarDataBase.db";
        this.url="jdbc:sqlite:" + path;
        this.conn=null;
    }
    public void connect()
    {
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void createTable(String name, String fields)
    {
        String sql = "CREATE TABLE IF NOT EXISTS " + name + fields;
        try{
            conn = DriverManager.getConnection(url);
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
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /* request insert
    INSERT INTO messages(id_msg,id_conv,id_user,msg,date) VALUES(?,?,?,?,?)
     */

    public void update(String name, String field_value, String id)
    {
        String sql = "UPDATE " + name + " SET " + field_value + " WHERE " + id;
        try {
            conn = DriverManager.getConnection(url);
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

    public void delete(String name, String id)
    {
        String sql = "DELETE FROM " + name + " WHERE " + id;
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /* request delete
    DELETE FROM warehouses WHERE id = ?
     */

    public ResultSet select(String field, String name)
    {
        String sql = "SELECT " + field + " FROM " + name;
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
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

}
