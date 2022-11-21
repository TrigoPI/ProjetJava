package Application.CLI;

import Application.CLI.Modules.LogsModuleCLI;
import Application.CLI.Modules.ModuleCLI;
import Application.CLI.Modules.ModuleNetworkCLI;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Utils.Log.Log;

import java.util.HashMap;
import java.util.Scanner;

public class CLI
{
    private static CLI instance = null;

    private HashMap<String, ModuleCLI> modules;

    private CLI()
    {
        Log.off();
        Log.savingOn();

        this.modules = new HashMap<String, ModuleCLI>();
    }

    public void addModule(String name, ModuleCLI module)
    {
        if (!this.modules.containsKey(name)) this.modules.put(name, module);
    }

    private String getUserInput(String msg)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print(msg);
        return scanner.nextLine();
    }

    private void execCmd(String cmd)
    {
        String[] cmdSplit = cmd.split(" ");

        if (this.modules.containsKey(cmdSplit[0]))
        {
            if (cmdSplit.length > 1)
            {
                this.modules.get(cmdSplit[0]).execCmd(cmdSplit[1]);
            }
        }
        else
        {
            System.out.println("unknown command");
        }
    }

    public void run()
    {
        String input = "";

        while (!input.equals("exit"))
        {
            input = getUserInput("> ");
            this.execCmd(input);
        }
    }

    public static void createCLI()
    {
        if (instance == null) instance = new CLI();

        instance.addModule("network", new ModuleNetworkCLI(new NetworkManager(4000, 5000)));
        instance.addModule("log", new LogsModuleCLI());

        instance.run();
    }
}
