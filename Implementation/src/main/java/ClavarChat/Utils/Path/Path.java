package ClavarChat.Utils.Path;

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
}
