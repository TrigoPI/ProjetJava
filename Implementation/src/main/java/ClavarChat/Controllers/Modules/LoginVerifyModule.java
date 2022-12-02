package ClavarChat.Controllers.Modules;

import ClavarChat.Controllers.NetworkAPI.NetworkAPI;
import ClavarChat.Models.Callback.Callback;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage.MESSAGE_TYPE;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;

public class LoginVerifyModule extends Handler
{
    private int tcpPort;

    private UserManager userManager;
    private NetworkAPI networkAPI;
    private Callback callback;

    public LoginVerifyModule(NetworkAPI networkAPI, UserManager userManager, int tcpPort)
    {
        this.tcpPort = tcpPort;

        this.callback = null;
        this.userManager = userManager;
        this.networkAPI = networkAPI;
    }

    public void setCallback(Callback callback)
    {
        this.callback = callback;
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
                this.networkAPI.sendTCP(dst.get(0), this.tcpPort, new ClavarChatMessage(user, MESSAGE_TYPE.LOGIN));
            }

            this.userManager.setLogged(true);
            if (this.callback != null) this.callback.call();
        }
        else
        {
            Log.Error(this.getClass().getName() + " Pseudo already used");
            this.userManager.setLogged(false);
        }
    }
}
