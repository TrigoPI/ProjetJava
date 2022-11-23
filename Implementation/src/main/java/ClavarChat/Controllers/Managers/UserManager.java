package ClavarChat.Controllers.Managers;

import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;

import java.util.*;

public class UserManager
{
    private boolean logged;
    private int userCount;

    private UserData user;
    private ArrayList<UserData> users;
    private HashMap<String, String> ipTable;

    public UserManager()
    {
        this.user = new UserData("", "");
        this.users = new ArrayList<UserData>();
        this.ipTable = new HashMap<String, String>();

        this.userCount = 1;
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

        moduleCLI.addCommand("list-user", () -> {
            for (UserData user : this.users)
            {
                System.out.println( this.ipTable.get(user.pseudo) + " --> " + user.pseudo + " " + user.id);
            }
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

    public int getUserCount()
    {
        return userCount;
    }

    public UserData getUser()
    {
        return this.user;
    }

    public void addUser(UserData user, String src)
    {
        this.ipTable.put(user.pseudo, src);
        this.users.add(user);
    }

    public void setUser(String pseudo, String id)
    {
        this.logged = true;
        this.user.pseudo = pseudo;
        this.user.id = id;
    }
}
