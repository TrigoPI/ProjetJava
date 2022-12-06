package ClavarChat.Controllers.Managers.User;

import ClavarChat.Models.Users.User;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class UserManager
{
    private boolean logged;
    private int userCount;

    private final User user;
    private final HashMap<String, User> users;
    private final HashMap<String, ArrayList<String>> ipTable;

    public UserManager()
    {
        this.user = new User("", "");
        this.users = new HashMap<>();
        this.ipTable = new HashMap<>();

        this.userCount = 1;
        this.logged = false;
    }

    public void reset()
    {
        this.user.pseudo = "";
        this.user.id = "";

        this.users.clear();
        this.ipTable.clear();

        this.userCount = 1;
    }

    public void setLogged(boolean logged)
    {
        this.logged = logged;
    }

    public void addUser(User user, String src)
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

    public ArrayList<User> getUsers()
    {
        ArrayList<User> users = new ArrayList<User>();
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

    public User getUser()
    {
        return this.user;
    }
}