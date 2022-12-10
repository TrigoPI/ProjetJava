package ClavarChat.Controllers.Chain;

import ClavarChat.Controllers.NetworkAPI.NetworkAPI;
import ClavarChat.Models.ByteImage.ByteImage;
import ClavarChat.Models.ChainData.Request.LoginRequest;
import ClavarChat.Models.ChainData.Request.Request;
import ClavarChat.Models.ChainData.Response.Response;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.ClavarChatMessage.LoginMessage;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;
import javafx.scene.image.Image;

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
        Log.Print(this.getClass().getName() + " Checking if pseudo exist");

        LoginRequest loginRequest = (LoginRequest)request;

        User user = new User(loginRequest.pseudo, loginRequest.id);
        Image avatar = this.userManager.getAvatar();
        ByteImage img = ByteImage.encode(avatar.getUrl());

        ArrayList<User> users = this.userManager.getUsers();

        if (this.userManager.userExist(user.pseudo))
        {
            Log.Error(this.getClass().getName() + " Pseudo already used");

            this.userManager.setLogged(false);
            this.userManager.reset();

            return Response.INVALID_PSEUDO;
        }

        for (User other : users)
        {
            ArrayList<String> dst = this.userManager.getUserIP(other.pseudo);
            this.networkAPI.sendTCP(dst.get(0), this.tcpPort, new LoginMessage(LoginMessage.LOGIN, user.pseudo, user.id, img));
        }

        this.userManager.setUser(loginRequest.pseudo, loginRequest.id);
        this.userManager.setLogged(true);

        return Response.VALID_PSEUDO;
    }
}
