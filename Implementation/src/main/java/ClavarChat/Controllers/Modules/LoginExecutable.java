package ClavarChat.Controllers.Modules;

import ClavarChat.Controllers.Managers.Thread.ThreadExecutable;

public class LoginExecutable implements ThreadExecutable
{
    private DiscoverModule discoverModule;

    public LoginExecutable(DiscoverModule discoverModule)
    {
        this.discoverModule = discoverModule;
    }

    @Override
    public void run()
    {
        this.discoverModule.handle();
    }
}
