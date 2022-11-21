package Application.CLI.Modules;

import ClavarChat.Utils.Log.Log;

public class LogsModuleCLI extends ModuleCLI
{
    public LogsModuleCLI()
    {
        this.addCommand("logs");
    }

    @Override
    protected void command(String cmd)
    {
        switch (cmd)
        {
            case "logs":
                Log.displayLogs();
                break;
        }
    }
}
