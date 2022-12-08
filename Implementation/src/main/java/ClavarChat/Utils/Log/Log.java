package ClavarChat.Utils.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Log
{
    private static String path = "./src/main/java/ClavarChat/Utils/Log/log.txt";
    private static boolean active = true;
    private static boolean save = false;
    private static final ArrayList<String> logs = new ArrayList<String>();

    public static void clearLogFile()
    {
        try
        {
            PrintWriter  writer = new PrintWriter(path);
            writer.print("");
            writer.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

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
        String msgFormat = "[" + date + "] " + a;

//        writeLog(msgFormat);

        if (save) logs.add(color + msgFormat + ConsoleColors.RESET);
        if (active) System.out.println(color + msgFormat + ConsoleColors.RESET);
    }

    private static void writeLog(String log)
    {
        try
        {
            FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println(log);
            pw.flush();

            pw.close();
            bw.close();
            fw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
