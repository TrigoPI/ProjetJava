package ClavarChat.Utils.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
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
}
