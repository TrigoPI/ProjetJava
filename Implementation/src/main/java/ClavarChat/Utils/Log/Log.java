package ClavarChat.Utils.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log
{
    private static boolean active = true;

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

    private static void format(String color, String a)
    {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String date = format.format(new Date());
        String msgFormat = "[" + date + "] " + a;

        if (active) System.out.println(color + msgFormat + ConsoleColors.RESET);
    }
}
