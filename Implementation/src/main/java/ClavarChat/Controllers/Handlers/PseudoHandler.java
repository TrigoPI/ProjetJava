package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.Managers.User.UserManager;

public class PseudoHandler
{
    private final UserManager userManager;

    public PseudoHandler(UserManager userManager)
    {
        this.userManager = userManager;
    }
}
