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
    private final HashMap<String, UserData> users;

    public UserManager()
    {
        this.user = new User("", "");
        this.users = new HashMap<>();

        this.userCount = 1;
        this.logged = false;
    }

    public ArrayList<User> getUsers()
    {
        ArrayList<User> users = new ArrayList<>();
        for (String key : this.users.keySet()) users.add(this.users.get(key).user);
        return users;
    }

    public ArrayList<String> getUserIP(String pseudo)
    {
        if (!this.userExist(pseudo)) return null;
        return this.users.get(pseudo).addresses;
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

    public void reset()
    {
        this.user.pseudo = "";
        this.user.id = "";
        this.users.clear();
        this.userCount = 1;
    }

    public void setLogged(boolean logged)
    {
        this.logged = logged;
    }

    public void addUser(User user, String ip)
    {

        if (!this.userExist(user.pseudo))
        {
            Log.Info(this.getClass().getName() + " Adding new user : " + user.pseudo + " / #" + user.id);
            this.users.put(user.pseudo, new UserData(user));
            this.userCount++;
        }

        Log.Info(this.getClass().getName() + " Adding ip : " + ip + " to user : " + user.pseudo + " / #" + user.id);

        UserData userData = this.users.get(user.pseudo);
        userData.addresses.add(ip);
    }

    public void setUser(String pseudo, String id)
    {
        Log.Print(this.getClass().getName() + " Register main user : " + pseudo + " / #" + id);
        this.user.pseudo = pseudo;
        this.user.id = id;
    }

    private class UserData
    {
        public User user;
        public ArrayList<String> addresses;

        public UserData(User user)
        {
            this.user = user;
            this.addresses = new ArrayList<>();
        }
    }
}