package ClavarChat.Controllers.Managers.DataBase;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.PackedArray.PackedArray;

import java.sql.*;
import java.util.ArrayList;

public class DataBaseManager
{
    private final String url;
    private final PackedArray<PreparedStatement> preparedStatements;
    private final PackedArray<ResultSet> resultsSet;
    private Connection conn;

    public DataBaseManager(String path)
    {
        this.url = "jdbc:sqlite:" + path;

        this.preparedStatements = new PackedArray<>();
        this.resultsSet = new PackedArray<>();

        this.connect();
    }

    public ArrayList<Integer> decodeAsInt(int resultSetId, int columnIndex)
    {
        ArrayList<Integer> datas = new ArrayList<>();
        ResultSet result = this.resultsSet.get(resultSetId);

        if (result == null)
        {
            Log.Error(this.getClass().getName() + " No resultsSet with id : " + resultSetId);
            return null;
        }

        try
        {
            while (result.next())
            {
                int data = result.getInt(columnIndex);
                datas.add(data);
            }
        }
        catch (SQLException e)
        {
            Log.Error(DataBaseAPI.class.getName() + " " + e.getMessage());
        }

        return datas;
    }

    public ArrayList<String> decodeAsString(int resultSetId, int columnIndex)
    {
        ArrayList<String> datas = new ArrayList<>();
        ResultSet result = this.resultsSet.get(resultSetId);

        if (result == null)
        {
            Log.Error(this.getClass().getName() + " No resultsSet with id : " + resultSetId);
            return null;
        }

        try
        {
            while (result.next())
            {
                String data = result.getString(columnIndex);
                datas.add(data);
            }
        }
        catch (SQLException e)
        {
            Log.Error(DataBaseAPI.class.getName() + " " + e.getMessage());
        }

        return datas;
    }

    public ArrayList<byte[]> decodeAsBytes(int resultSetId, int columnIndex)
    {
        ArrayList<byte[]> datas = new ArrayList<>();
        ResultSet result = this.resultsSet.get(resultSetId);

        if (result == null)
        {
            Log.Error(this.getClass().getName() + " No resultsSet with id : " + resultSetId);
            return null;
        }

        try
        {
            while (result.next())
            {
                byte[] data = result.getBytes(columnIndex);
                datas.add(data);
            }
        }
        catch (SQLException e)
        {
            Log.Error(DataBaseAPI.class.getName() + " " + e.getMessage());
        }

        return datas;
    }

    public int createPreparedStatement(String request)
    {
        int id = -1;

        try
        {
            PreparedStatement preparedStatement = this.conn.prepareStatement(request);
            id = this.preparedStatements.add(preparedStatement);
        }
        catch (SQLException e)
        {
            Log.Error(this.getClass().getName() + " " + e.getMessage());
        }

        return id;
    }

    public int executeQuery(String request, Object ...value)
    {
        int id = -1;
        String requestFormat = String.format(request, value);
        Log.Print(this.getClass().getName() + " Executing : " + requestFormat);

        try
        {
            Statement statement = conn.createStatement();
            id = this.resultsSet.add(statement.executeQuery(requestFormat));
        }
        catch (SQLException e)
        {
            Log.Error(this.getClass().getName() + " " + e.getMessage());
        }

        return id;
    }

    public int getIdGenerated(int preparedStatementId)
    {
        PreparedStatement statement = this.preparedStatements.get(preparedStatementId);
        int id = -1;

        if (statement == null)
        {
            Log.Error(this.getClass().getName() + " No preparedStatementId with id : " + preparedStatementId);
            return -1;
        }

        try
        {
            id = statement.getGeneratedKeys().getInt(1);
        }
        catch (SQLException e)
        {
            Log.Error(this.getClass().getName() + " " + e.getMessage());
        }

        return id;
    }

    public void connect()
    {
        try
        {
            conn = DriverManager.getConnection(this.url);
            Log.Info(this.getClass().getName() + " Connection to SQLite has been established.");

        }
        catch (SQLException e)
        {
            Log.Error(this.getClass().getName() + " " + e.getMessage());
        }
    }

    public void execute(String request, Object ...value)
    {
        String requestFormat = String.format(request, value);
        Log.Print(this.getClass().getName() + " Executing : " + requestFormat);

        try
        {
            Statement statement = conn.createStatement();
            statement.execute(requestFormat);
        }
        catch (SQLException e)
        {
            Log.Error(this.getClass().getName() + " " + e.getMessage());
        }
    }

    public void executePreparedStatement(int preparedStatementId)
    {
        PreparedStatement preparedStatement = this.preparedStatements.get(preparedStatementId);

        if (preparedStatement == null)
        {
            Log.Error(this.getClass().getName() + " No preparedStatement with id : " + preparedStatementId);
            return;
        }

        try
        {
            Log.Info(this.getClass().getName() + " Executing preparedStatement with id : " + preparedStatementId);
            preparedStatement.execute();
        }
        catch (SQLException e)
        {
            Log.Error(this.getClass().getName() + " " + e.getMessage());
        }
    }

    public void setString(int preparedStatementId, int index, String value)
    {
        PreparedStatement preparedStatement = this.preparedStatements.get(preparedStatementId);

        if (preparedStatement == null)
        {
            Log.Error(this.getClass().getName() + " No preparedStatement with id : " + preparedStatementId);
            return;
        }

        try
        {
            Log.Print(this.getClass().getName() + " Setting STRING to preparedStatement width id : " + preparedStatementId + " value : " + value);
            preparedStatement.setString(index, value);
        }
        catch (SQLException e)
        {
            Log.Error(this.getClass().getName() + " ERROR setting STRING with preparedStatement id : " + preparedStatementId + " --> " + e.getMessage());
        }
    }

    public void setBytes(int preparedStatementId, int index, byte[] value)
    {
        PreparedStatement preparedStatement = this.preparedStatements.get(preparedStatementId);

        if (preparedStatement == null)
        {
            Log.Error(this.getClass().getName() + " No preparedStatement with id : " + preparedStatementId);
            return;
        }

        try
        {
            Log.Print(this.getClass().getName() + " Setting BLOB to preparedStatement width id : " + preparedStatementId + " value : " + value);
            preparedStatement.setBytes(index, value);
        }
        catch (SQLException e)
        {
            Log.Error(this.getClass().getName() + " ERROR setting BLOB with preparedStatement id : " + preparedStatementId + " --> " + e.getMessage());
        }
    }

    public void setInt(int preparedStatementId, int index, int value)
    {
        PreparedStatement preparedStatement = this.preparedStatements.get(preparedStatementId);

        if (preparedStatement == null)
        {
            Log.Error(this.getClass().getName() + " No preparedStatement with id : " + preparedStatementId);
            return;
        }

        try
        {
            Log.Print(this.getClass().getName() + " Setting INT to preparedStatement width id : " + preparedStatementId + " value : " + value);
            preparedStatement.setInt(index, value);
        }
        catch (SQLException e)
        {
            Log.Error(this.getClass().getName() + " ERROR setting int with preparedStatement id : " + preparedStatementId + " --> " + e.getMessage());
        }
    }

    public void removeResultSet(int resultSetId)
    {
        ResultSet preparedStatement = this.resultsSet.get(resultSetId);

        if (preparedStatement == null)
        {
            Log.Error(this.getClass().getName() + " No resultsSet with id : " + resultSetId);
            return;
        }

        this.resultsSet.remove(resultSetId);
    }

    public void removePreparedStatement(int preparedStatementId)
    {
        PreparedStatement preparedStatement = this.preparedStatements.get(preparedStatementId);

        if (preparedStatement == null)
        {
            Log.Error(this.getClass().getName() + " No preparedStatement with id : " + preparedStatementId);
            return;
        }

        this.preparedStatements.remove(preparedStatementId);
    }
}
