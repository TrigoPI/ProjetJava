package ClavarChat.Controllers.Chain;

import ClavarChat.Controllers.API.NetworkAPI.NetworkAPI;
import ClavarChat.Models.ChainData.Response;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;

public class PseudoVerify extends Handler
{
    private final UserManager userManager;
    private final NetworkAPI networkAPI;

    public PseudoVerify(NetworkAPI networkAPI, UserManager userManager)
    {
        this.userManager = userManager;
        this.networkAPI = networkAPI;
    }

    @Override
    public String handle()
    {
        Log.Print(this.getClass().getName() + " Checking if pseudo exist");

        User mainUser = this.userManager.getUser();

        if (this.userManager.pseudoExist(mainUser.pseudo))
        {
            Log.Error(this.getClass().getName() + " Pseudo already used");

            this.userManager.setLogged(false);
            this.userManager.reset();

            return Response.INVALID_PSEUDO;
        }

        this.networkAPI.sendLogin();
        this.userManager.setLogged(true);

        return Response.VALID_PSEUDO;
    }
}
