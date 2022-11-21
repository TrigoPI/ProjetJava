package ClavarChat.Utils.CLI.Modules;

import ClavarChat.Utils.Log.Log;

public class ModuleLogsCLI extends ModuleCLI
{
    public ModuleLogsCLI()
    {
        this.addCommand("logs", () -> {
            Log.displayLogs();
        });
    }
}
