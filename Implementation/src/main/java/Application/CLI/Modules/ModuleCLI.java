package Application.CLI.Modules;

import java.util.ArrayList;

public abstract class ModuleCLI
{
    private ArrayList<String> commands;

    protected ModuleCLI()
    {
        this.commands = new ArrayList<String>();
        this.commands.add("list");
    }

    protected abstract void command(String cmd);

    public void execCmd(String cmd)
    {
        if (this.commands.contains(cmd))
        {
            switch (cmd.toLowerCase())
            {
                case "list":
                    this.cmdList();
                    break;
                default:
                    this.command(cmd);
                    break;
            }
        }
        else
        {
            this.unknownCmd();
        }
    }

    protected void addCommand(String cmd)
    {
        if (!this.commands.contains(cmd)) this.commands.add(cmd);
    }

    private void unknownCmd()
    {
        System.out.println("unknown command");
    }


    private void cmdList()
    {
        for (String command : this.commands) System.out.println(command);
    }
}
