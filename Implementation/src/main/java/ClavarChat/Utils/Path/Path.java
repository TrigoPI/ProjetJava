package ClavarChat.Utils.Path;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Path
{
    public static String getWorkingPath()
    {
        String path = null;

        try
        {
            path = new java.io.File(".").getCanonicalPath();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return path;
    }

    public static String getFileExtension(String fileName)
    {
        return FilenameUtils.getExtension(fileName);
    }

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

    public static JSONObject parseJSON(String path)
    {
        JSONObject obj;

        try
        {
            JSONParser parser = new JSONParser();
            FileReader fileReader = new FileReader(path);
            obj = (JSONObject)parser.parse(fileReader);
        }
        catch (IOException | ParseException e)
        {
            throw new RuntimeException(e);
        }

        return obj;
    }
}
