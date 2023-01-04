package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.EventAPI.EventAPI;
import ClavarChat.Controllers.API.NetworkAPI.NetworkAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Utils.Log.Log;

public class PseudoHandler
{
    private final UserManager userManager;
    private final NetworkAPI networkAPI;

    public PseudoHandler(UserManager userManager, NetworkAPI networkAPI)
    {
        this.userManager = userManager;
        this.networkAPI = networkAPI;
    }

    public void changePseudo(String newPseudo)
    {

    }

    public boolean checkPseudo()
    {
        String pseudo = this.userManager.getPseudo();

        if (this.userManager.pseudoExist(pseudo))
        {
            Log.Error(PseudoHandler.class.getName() + " Pseudo already used");

            this.userManager.setLogged(false);
            this.userManager.reset();

            return false;
        }

        Log.Info(PseudoHandler.class.getName() + " Pseudo Success");
        this.networkAPI.sendLogin();
        this.userManager.setLogged(true);

        return true;
    }
}
