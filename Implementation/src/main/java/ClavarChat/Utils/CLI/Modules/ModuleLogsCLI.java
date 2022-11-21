package ClavarChat.Utils.CLI.Modules;

import ClavarChat.Utils.Log.Log;

public class ModuleLogsCLI extends ModuleCLI
{
    public ModuleLogsCLI()
    {
        this.addCommand("logs", () -> {
            Log.displayLogs();
        });

        this.addCommand("continue", () -> {
            String input = "";

            Log.displayLogs();
            Log.on();

            while (!input.equals("q"))
            {
                input = this.getUserInput("");
            }

            Log.off();
        });
    }
}
