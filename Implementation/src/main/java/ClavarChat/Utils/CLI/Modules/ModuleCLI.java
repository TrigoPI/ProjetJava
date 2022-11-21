package ClavarChat.Utils.CLI.Modules;

import ClavarChat.Utils.CLI.Command.Command;

import java.util.HashMap;
import java.util.Scanner;

public abstract class ModuleCLI
{
    private HashMap<String, Command> commands;

    protected ModuleCLI()
    {
        this.commands = new HashMap<String, Command>();
    }

    public void execCommand(String name)
    {
        if (this.commands.containsKey(name))
        {
            this.commands.get(name).exec();
        }
        else
        {
            System.out.println("Unknown command");
        }
    }

    public String getUserInput(String msg)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print(msg);
        return scanner.nextLine();
    }

    public void addCommand(String name, Command command)
    {
        if (!this.commands.containsKey(name)) this.commands.put(name, command);
    }
}
