package ClavarChat.Controllers.Modules;

import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage.MESSAGE_TYPE;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Controllers.Managers.UserManager;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;

public class LoginVerifyModule extends Handler
{
    private UserManager userManager;
    private NetworkManager networkManager;

    public LoginVerifyModule(NetworkManager networkManager, UserManager userManager)
    {
        this.userManager = userManager;
        this.networkManager = networkManager;
    }

    @Override
    public void handle()
    {
        UserData user = this.userManager.getUser();

        if (!this.userManager.userExist(user.pseudo))
        {
            ArrayList<UserData> users = this.userManager.getUsers();

            for (UserData other : users)
            {
                ArrayList<String> dst = this.userManager.getUserIP(other.pseudo);
                this.networkManager.sendTCP(new ClavarChatMessage(user, MESSAGE_TYPE.LOGIN), dst.get(0));
            }

            this.userManager.setLogged(true);
        }
        else
        {
            Log.Error(this.getClass().getName() + " Pseudo already used");
        }
    }
}
