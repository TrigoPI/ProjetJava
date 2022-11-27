package Application.ClavaChatCLI;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;
import ClavarChat.Utils.Log.Log;

public class ClavarChatCLI
{
    public static void addModuleLogCLI()
    {
        Log.off();
        Log.savingOn();

        ModuleCLI moduleLogsCLI = new ModuleCLI();

        moduleLogsCLI.addCommand("logs", () -> {
            Log.displayLogs();
        });

        moduleLogsCLI.addCommand("continue", () -> {
            String input = "";

            Log.displayLogs();
            Log.on();

            while (!input.equals("q"))
            {
                input = moduleLogsCLI.getUserInput("");
            }

            Log.off();
        });

        CLI.installModule("log", moduleLogsCLI);
    }

    public static void init()
    {
        addModuleLogCLI();
        new ClavarChatAPI(8080, 7070);
    }

    public static void runApplication()
    {
        CLI.createCLI();
        init();
        CLI.start();
    }
}
