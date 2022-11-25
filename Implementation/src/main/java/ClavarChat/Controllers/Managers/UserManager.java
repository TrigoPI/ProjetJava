package ClavarChat.Controllers.Managers;

import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;
import ClavarChat.Utils.Log.Log;

import java.util.*;

public class UserManager
{
    private boolean logged;
    private int userCount;

    private UserData user;
    private HashMap<String, UserData> users;
    private HashMap<String, ArrayList<String>> ipTable;

    public UserManager()
    {
        this.user = new UserData("", "");
        this.users = new HashMap<String, UserData>();
        this.ipTable = new HashMap<String, ArrayList<String>>();

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
            for (String key : this.users.keySet())
            {
                UserData user = this.users.get(key);
                System.out.println(this.ipTable.get(user.pseudo) + " --> " + user.pseudo + " " + user.id);
            }
        });

        moduleCLI.addCommand("get-user", () -> {
            System.out.println("User : " + this.user.pseudo);
            System.out.println("ID : " + this.user.id);
        });

        CLI.installModule("user", moduleCLI);
    }

    public void setLogged(boolean logged)
    {
        this.logged = logged;
    }

    public void addUser(UserData user, String src)
    {
        Log.Info(this.getClass().getName() + " Adding new user : " + user.pseudo + " / #" + user.id);

        if (!this.ipTable.containsKey(user))
        {
            this.ipTable.put(user.pseudo, new ArrayList<String>());
            this.users.put(user.pseudo, user);
        }

        this.ipTable.get(user.pseudo).add(src);
        this.userCount++;
    }

    public void setUser(String pseudo, String id)
    {
        Log.Print(this.getClass().getName() + " Register main user : " + pseudo + " / #" + id);
        this.user.pseudo = pseudo;
        this.user.id = id;
    }

    public ArrayList<UserData> getUsers()
    {
        ArrayList<UserData> users = new ArrayList<UserData>();
        for (String key : this.users.keySet()) users.add(this.users.get(key));
        return users;
    }

    public ArrayList<String> getUserIP(String pseudo)
    {
        if (!this.userExist(pseudo)) return new ArrayList<>();
        return this.ipTable.get(pseudo);
    }

    public boolean isLogged()
    {
        return this.logged;
    }

    public boolean userExist(String pseudo)
    {
        return this.users.containsKey(pseudo);
    }

    public int getUserCount()
    {
        return userCount;
    }

    public UserData getUser()
    {
        return this.user;
    }
}