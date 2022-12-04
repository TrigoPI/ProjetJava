package ClavarChat.Controllers.Chain;

import ClavarChat.Controllers.NetworkAPI.NetworkAPI;
import ClavarChat.Models.ChainData.Request.LoginRequest;
import ClavarChat.Models.ChainData.Request.Request;
import ClavarChat.Models.ChainData.Response.Response;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage.MESSAGE_TYPE;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;

public class PseudoVerify extends Handler
{
    private final int tcpPort;

    private final UserManager userManager;
    private final NetworkAPI networkAPI;

    public PseudoVerify(NetworkAPI networkAPI, UserManager userManager, int tcpPort)
    {
        this.tcpPort = tcpPort;

        this.userManager = userManager;
        this.networkAPI = networkAPI;
    }

    @Override
    public String handle(Request request)
    {
        LoginRequest loginRequest = (LoginRequest)request;
        UserData user = new UserData(loginRequest.pseudo, loginRequest.id);

        if (!this.userManager.userExist(user.pseudo))
        {
            ArrayList<UserData> users = this.userManager.getUsers();

            for (UserData other : users)
            {
                ArrayList<String> dst = this.userManager.getUserIP(other.pseudo);
                this.networkAPI.sendTCP(dst.get(0), this.tcpPort, new ClavarChatMessage(user, MESSAGE_TYPE.LOGIN));
            }

            this.userManager.setUser(loginRequest.pseudo, loginRequest.id);
            this.userManager.setLogged(true);

            return Response.VALID_PSEUDO;
        }
        else
        {
            Log.Error(this.getClass().getName() + " Pseudo already used");
            this.userManager.setLogged(false);
        }

        return Response.INVALID_PSEUDO;
    }
}
