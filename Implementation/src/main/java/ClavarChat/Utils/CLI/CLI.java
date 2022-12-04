package ClavarChat.Utils.CLI;

import ClavarChat.Utils.CLI.Modules.ModuleCLI;
import ClavarChat.Utils.Log.Log;

import java.util.HashMap;
import java.util.Scanner;

public class CLI
{
    private static CLI instance = null;

    private final HashMap<String, ModuleCLI> modules;

    private CLI()
    {
        this.modules = new HashMap<String, ModuleCLI>();
    }

    private void addModule(String name, ModuleCLI module)
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
                this.modules.get(cmdSplit[0]).execCommand(cmdSplit[1]);
            }
        }
        else
        {
            System.out.println("unknown module");
        }
    }

    private void run()
    {
        String input = "";

        while (!input.equals("exit"))
        {
            input = getUserInput("> ");
            this.execCmd(input);
        }
    }

    public static void installModule(String name, ModuleCLI moduleCLI)
    {
        if (instance != null)
        {
            instance.addModule(name, moduleCLI);
        }
        else
        {
            System.out.println("CLI not instanced");
        }
    }

    public static void createCLI()
    {
        if (instance == null) instance = new CLI();
    }

    public static void start()
    {
        instance.run();
    }
}
