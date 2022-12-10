package ClavarChat.Controllers.Managers.User;

import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;

public class UserManager
{
    private User user;
    private Image avatar;

    private final HashMap<String, UserData> users;

    private boolean logged;

    public UserManager()
    {
        this.user = null;
        this.avatar = null;
        this.users = new HashMap<>();

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
        return this.users.size() + 1;
    }

    public User getUser()
    {
        return this.user;
    }

    public User getUser(String pseudo)
    {
        return (!this.users.containsKey(pseudo))?null:this.users.get(pseudo).user;
    }

    public Image getAvatar()
    {
        return this.avatar;
    }

    public Image getAvatar(String pseudo)
    {
        return (!this.users.containsKey(pseudo))?null:this.users.get(pseudo).avatar;
    }

    public void setLogged(boolean logged)
    {
        this.logged = logged;
    }

    public void setUser(String pseudo, String id)
    {
        Log.Print(this.getClass().getName() + " Register main user : " + pseudo + " / #" + id);
        this.user = new User(pseudo, id);
    }

    public void setAvatar(String path)
    {
        Log.Print(this.getClass().getName() + " Set avatar : " + path + " to user");
        this.avatar = new Image(path);
    }

    public void setAvatar(String pseudo, Image image)
    {
        Log.Print(this.getClass().getName() + " Set avatar to user : " + pseudo);
        if (this.users.containsKey(pseudo)) this.users.get(pseudo).avatar = image;
    }

    public void addUser(User user, String ip)
    {

        if (!this.userExist(user.pseudo))
        {
            Log.Info(this.getClass().getName() + " Adding new user : " + user.pseudo + " / #" + user.id);
            this.users.put(user.pseudo, new UserData(user));
        }

        Log.Info(this.getClass().getName() + " Adding ip : " + ip + " to user : " + user.pseudo + " / #" + user.id);

        UserData userData = this.users.get(user.pseudo);
        userData.addresses.add(ip);
    }

    public void removeUser(String pseudo)
    {
        if (this.userExist(pseudo))
        {
            Log.Print(this.getClass().getName() + " Removing user : " + pseudo);
            this.users.remove(pseudo);
        }
        else
        {
            Log.Print(this.getClass().getName() + " No user with pseudo : " + pseudo);
        }
    }

    public void reset()
    {
        Log.Print(this.getClass().getName() + " Resetting User Manager");
        this.user = null;
        this.users.clear();
    }

    private class UserData
    {
        public User user;
        public Image avatar;
        public ArrayList<String> addresses;

        public UserData(User user)
        {
            this.user = user;
            this.avatar = null;
            this.addresses = new ArrayList<>();
        }
    }
}