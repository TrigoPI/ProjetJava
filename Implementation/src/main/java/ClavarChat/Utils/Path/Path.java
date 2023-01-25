package ClavarChat.Utils.Path;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.commons.io.FileUtils;

import java.io.*;

public class Path
{
    public static byte[] getBytes(String fileName)
    {
        byte[] buffer = null;

        try
        {
            File file = new File(fileName);
            buffer = FileUtils.readFileToByteArray(file);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return buffer;
    }

    public static JSONObject parseJSON(InputStream in)
    {
        JSONObject obj;
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader buffer = new BufferedReader(reader);

        try
        {
            JSONParser parser = new JSONParser();
            obj = (JSONObject)parser.parse(buffer);
        }
        catch (IOException | ParseException e)
        {
            throw new RuntimeException(e);
        }

        return obj;
    }

    public static InputStream getFileInputStream(String path)
    {
        InputStream in;
        File file = new File(path);
        try
        {
            in = new FileInputStream(file);
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }

        return in;
    }
}
