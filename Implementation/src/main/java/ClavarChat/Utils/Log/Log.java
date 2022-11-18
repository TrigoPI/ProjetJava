package ClavarChat.Utils.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log
{
    private static boolean active = true;

    private static void format(String color, String a)
    {
        if (active)
        {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            String date = format.format(new Date());
            System.out.println(color + "[" + date + "] " + a + ConsoleColors.RESET);
        }
    }

    public static void on()
    {
        active = true;
    }

    public static void off()
    {
        active = false;
    }

    public static void Print(String a)
    {
        Log.format(ConsoleColors.WHITE, a);
    }

    public static void Info(String a)
    {
        Log.format(ConsoleColors.GREEN, a);
    }

    public static void Warning(String a)
    {
        Log.format(ConsoleColors.YELLOW, a);
    }

    public static void Error(String a)
    {
        Log.format(ConsoleColors.RED, a);
    }
}
