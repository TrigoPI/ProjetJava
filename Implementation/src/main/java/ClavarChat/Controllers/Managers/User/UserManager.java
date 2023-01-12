package ClavarChat.Controllers.Managers.User;

import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.PackedArray.PackedArray;
import ClavarChat.Utils.Path.Path;
import Resources.Resources;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserManager
{
    private final String password;
    private final User user;
    private byte[] avatar;

    private final PackedArray<UserData> users;
    private final HashMap<Integer, Integer> idToIndex;
    private final HashMap<String, Integer> pseudoToIndex;

    private boolean logged;

    public UserManager()
    {
        JSONObject jsonObject = Path.parseJSON(Resources.CONFIG.CONF_FILE);
        String userId = (String)jsonObject.get("userId");

        this.user = new User("", Integer.parseInt(userId));
        this.password = (String)jsonObject.get("password");
        this.avatar = null;
        this.users = new PackedArray<>();
        this.idToIndex = new HashMap<>();
        this.pseudoToIndex = new HashMap<>();

        this.logged = false;
    }

    public boolean isConnected(int userId)
    {
        if (userId == this.user.id) return !this.isLogged();
        return this.idToIndex.containsKey(userId);
    }

    public boolean isLogged()
    {
        return !this.logged;
    }

    public boolean idExist(int userId)
    {
        return this.idToIndex.containsKey(userId);
    }

    public boolean pseudoExist(String pseudo)
    {
        return this.pseudoToIndex.containsKey(pseudo);
    }

    public int getUserCount()
    {
        return this.users.getDatas().size() + 1;
    }

    public int getId()
    {
        return this.user.id;
    }

    public int getId(String pseudo)
    {
        if (!this.pseudoToIndex.containsKey(pseudo))
        {
            Log.Warning(this.getClass().getName() + " No user pseudo : " + pseudo);
            return -1;
        }

        int id = this.pseudoToIndex.get(pseudo);
        UserData userData = this.users.get(id);
        return userData.user.id;
    }

    public ArrayList<User> getUsers()
    {
        ArrayList<User> allUsers = new ArrayList<>();
        for (UserData userData : this.users.getDatas()) allUsers.add(userData.user);
        return allUsers;
    }

    public ArrayList<String> getUserIP(int userId)
    {
        if (!this.idToIndex.containsKey(userId)) return null;
        int index = this.idToIndex.get(userId);
        UserData userData = this.users.get(index);
        return userData.addresses;
    }

    public User getUser()
    {
        return this.user;
    }

    public User getUser(int userId)
    {
        if (userId == this.user.id) return this.user;

        if (!this.idToIndex.containsKey(userId))
        {
            Log.Warning(this.getClass().getName() + " No user with id : " + userId);
            return null;
        }

        int index = this.idToIndex.get(userId);

        return this.users.get(index).user;
    }

    public String getPasswordHash()
    {
        return this.password;
    }

    public String getPseudo()
    {
        return this.user.pseudo;
    }

    public String getPseudo(int userId)
    {
        if (userId == this.user.id) return this.user.pseudo;

        if (!this.idToIndex.containsKey(userId))
        {
            Log.Warning(this.getClass().getName() + " No user with id : " + userId);
            return null;
        }

        int index = this.idToIndex.get(userId);

        return this.users.get(index).user.pseudo;
    }

    public byte[] getAvatar()
    {
        return this.avatar;
    }

    public byte[] getAvatar(int userId)
    {
        if (userId == this.user.id) return this.avatar;

        if (!this.idToIndex.containsKey(userId))
        {
            Log.Warning(this.getClass().getName() + " No user with id : " + userId);
            return null;
        }

        int index = this.idToIndex.get(userId);
        UserData userData = this.users.get(index);

        return userData.avatar;
    }

    public void setLogged(boolean logged)
    {
        this.logged = logged;
    }

    public void setUser(String pseudo, byte[] avatar)
    {
        Log.Print(this.getClass().getName() + " Register main user : " + pseudo + " / #" + this.user.id);
        this.avatar = avatar;
        this.user.pseudo = pseudo;
    }

    public void addUser(String pseudo, int userId, byte[] avatar)
    {
        if (this.idExist(userId))
        {
            Log.Error(this.getClass().getName() + " User : " + pseudo + " / #" + userId + " already register");
            return;
        }

        if (this.pseudoExist(pseudo))
        {
            Log.Error(this.getClass().getName() + " User : " + pseudo + " / #" + userId + " already register");
            return;
        }

        Log.Info(this.getClass().getName() + " Adding new user : " + pseudo + " / #" + userId);

        User newUser = new User(pseudo, userId);
        UserData userData = new UserData(newUser, avatar);

        int id = this.users.add(userData);
        this.idToIndex.put(userId, id);
        this.pseudoToIndex.put(pseudo, id);
    }

    public void addIpToUser(int userId, String ip)
    {
        if (!this.idToIndex.containsKey(userId))
        {
            Log.Error(this.getClass().getName() + " No user with id : " + userId);
            return;
        }

        Log.Info(this.getClass().getName() + " Adding ip : " + ip + " to user : " + userId);
        int index = this.idToIndex.get(userId);
        UserData userData = this.users.get(index);
        userData.addresses.add(ip);
    }

    public void changePseudo(int userId, String pseudo)
    {
        if (userId == this.user.id)
        {
            this.user.pseudo = pseudo;
            return;
        }

        if (!this.idExist(userId))
        {
            Log.Error(this.getClass().getName() + " No user with id : " + userId);
            return;
        }

        int index = this.idToIndex.get(userId);
        UserData userData = this.users.get(index);
        userData.user.pseudo = pseudo;
    }

    public void changeAvatar(int userId, byte[] avatar)
    {
        if (userId == this.user.id)
        {
            this.avatar = avatar;
            return;
        }

        if (!this.idExist(userId))
        {
            Log.Error(this.getClass().getName() + " No user with id : " + userId);
            return;
        }

        int index = this.idToIndex.get(userId);
        UserData userData = this.users.get(index);
        userData.avatar = avatar;
    }

    public void removeUser(int userId)
    {
        if (this.idToIndex.containsKey(userId))
        {
            Log.Print(this.getClass().getName() + " Removing user : " + userId);

            int index = this.idToIndex.get(userId);
            UserData userData = this.users.get(index);
            User user = userData.user;

            this.idToIndex.remove(userId);
            this.pseudoToIndex.remove(user.pseudo);
            this.users.remove(index);
        }
        else
        {
            Log.Print(this.getClass().getName() + " No user with id : " + userId);
        }
    }

    public void reset()
    {
        Log.Print(this.getClass().getName() + " Resetting User Manager");
        this.user.pseudo = "";
        this.users.clear();
        this.pseudoToIndex.clear();
        this.idToIndex.clear();
    }

    private static class UserData
    {
        public final User user;
        public byte[] avatar;
        public final ArrayList<String> addresses;

        public UserData(User user, byte[] avatar)
        {
            this.user = user;
            this.avatar = avatar;
            this.addresses = new ArrayList<>();
        }
    }
}