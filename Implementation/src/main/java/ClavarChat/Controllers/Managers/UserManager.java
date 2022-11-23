package ClavarChat.Controllers.Managers;

import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;

public class UserManager
{
    private boolean logged;
    private UserData user;

    public UserManager()
    {
        this.user = new UserData("", "");
        this.logged = false;

        this.DEBUG();
    }

    private void DEBUG()
    {
        ModuleCLI moduleCLI = new ModuleCLI();

        moduleCLI.addCommand("set-user", () -> {
            String pseudo = moduleCLI.getUserInput("Pseudo : ");
            String id = moduleCLI.getUserInput("id : ");

            this.setUser(pseudo, id);
        });

        moduleCLI.addCommand("get-user", () -> {
            System.out.println("User : " + this.user.pseudo);
            System.out.println("ID : " + this.user.id);
        });

        CLI.installModule("user", moduleCLI);
    }

    public boolean isLogged()
    {
        return this.logged;
    }

    public UserData getUser()
    {
        this.logged = true;
        return this.user;
    }

    public void setUser(String pseudo, String id)
    {
        this.user.pseudo = pseudo;
        this.user.id = id;
    }
}
