package ClavarChat.Controllers.Runnables.Discover;

import ClavarChat.Controllers.Handlers.DiscoverHandler;
import ClavarChat.Controllers.Managers.Thread.TMRunnable;

public class Discover implements TMRunnable
{
    private final DiscoverHandler discoverHandler;

    public Discover(DiscoverHandler discoverHandler)
    {
        this.discoverHandler = discoverHandler;
    }

    @Override
    public void run()
    {
        this.discoverHandler.discover();
    }
}
