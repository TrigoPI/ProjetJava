package ClavarChat.Controllers.Chain;

import ClavarChat.Controllers.NetworkAPI.NetworkAPI;
import ClavarChat.Models.ByteImage.ByteImage;
import ClavarChat.Models.ChainData.Response;
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
    public String handle()
    {
        Log.Print(this.getClass().getName() + " Checking if pseudo exist");

        User mainUser = this.userManager.getUser();
        Image image = this.userManager.getAvatar();
        ByteImage byteImage = ByteImage.encode(image.getUrl());
        ArrayList<User> users = this.userManager.getUsers();

        if (this.userManager.userExist(mainUser.pseudo))
        {
            Log.Error(this.getClass().getName() + " Pseudo already used");

            this.userManager.setLogged(false);
            this.userManager.reset();

            return Response.INVALID_PSEUDO;
        }

        for (User other : users)
        {
            ArrayList<String> dst = this.userManager.getUserIP(other.pseudo);
            this.networkAPI.sendTCP(dst.get(0), this.tcpPort, new LoginMessage(LoginMessage.LOGIN, mainUser.pseudo, mainUser.id, byteImage));
        }

        this.networkAPI.closeAllClients();
        this.userManager.setLogged(true);

        return Response.VALID_PSEUDO;
    }
}
