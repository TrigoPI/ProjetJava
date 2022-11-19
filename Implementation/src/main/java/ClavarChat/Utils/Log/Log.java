package ClavarChat.Utils.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Log
{
    private static boolean active = true;
    private static boolean save = false;
    private static ArrayList<String> logs = new ArrayList<String>();

    public static void savingOn()
    {
        save = true;
    }

    public static void savingOff()
    {
        save = false;
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

    public static void displayLogs()
    {
        for (String log : logs) System.out.println(log);
    }

    private static void format(String color, String a)
    {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String date = format.format(new Date());
        String msgFormat = color + "[" + date + "] " + a + ConsoleColors.RESET;

        if (save) logs.add(msgFormat);
        if (active) System.out.println(msgFormat);
    }
}
