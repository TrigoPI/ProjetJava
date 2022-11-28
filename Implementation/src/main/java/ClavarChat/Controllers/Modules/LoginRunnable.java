package ClavarChat.Controllers.Modules;

import ClavarChat.Controllers.Threads.ThreadRunnable;

public class LoginRunnable implements ThreadRunnable
{
    private DiscoverModule discoverModule;

    public LoginRunnable(DiscoverModule discoverModule)
    {
        this.discoverModule = discoverModule;
    }

    @Override
    public void run()
    {
        this.discoverModule.handle();
    }
}
