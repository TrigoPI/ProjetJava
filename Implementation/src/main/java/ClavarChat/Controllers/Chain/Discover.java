package ClavarChat.Controllers.Chain;

import ClavarChat.Controllers.NetworkAPI.NetworkAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.ChainData.Request.Request;
import ClavarChat.Models.ChainData.Response.Response;
import ClavarChat.Models.ClavarChatMessage.DiscoverRequestMessage;
import ClavarChat.Models.ClavarChatMessage.DiscoverResponseMessage;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Clock.Clock;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Discover extends Handler
{
    private final int udpPort;

    private final NetworkAPI networkAPI;
    private final UserManager userManager;
    private final Semaphore semaphore;

    private int userCount;
    private int responseCount;
    private final int timeout;

    public Discover(NetworkAPI networkAPI, UserManager userManager, int udpPort)
    {
        super();

        this.udpPort = udpPort;

        this.networkAPI = networkAPI;
        this.userManager = userManager;
        this.semaphore = new Semaphore(1);

        this.userCount = 0;
        this.responseCount = -1;
        this.timeout = 3;
    }

    public void onDiscoverInformation(DiscoverResponseMessage data, String src)
    {
        try
        {
            semaphore.acquire();

            if (this.responseCount == -1)
            {
                Log.Print(this.getClass().getName() + " Waiting for " + data.count + " responses");
                this.responseCount = 0;
                this.userCount = data.count;
            }

            this.responseCount++;

            semaphore.release();
        }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    public String handle(Request request)
    {
        this.broadcast();
        this.waitResponses();

        if (this.succeed() && this.next != null)
        {
            this.reset();
            return this.next.handle(request);
        }

        this.reset();
        return Response.DISCOVER_ERROR;
    }

    private void broadcast()
    {
        ArrayList<String> broadcast = this.networkAPI.getBroadcastAddresses();
        for (String addresse : broadcast) this.networkAPI.sendUDP(addresse, this.udpPort, new DiscoverRequestMessage());
    }

    private void waitResponses()
    {
        Log.Info(this.getClass().getName() + " Waiting...");
        Clock clock = new Clock();
        while (this.validUserCount() && clock.timeSecond() < this.timeout) {}
    }

    private void reset()
    {
        Log.Print(this.getClass().getName() + " Resetting discover");

        this.userCount = 0;
        this.responseCount = -1;
    }

    private boolean succeed()
    {
        if (this.responseCount == this.userCount || this.responseCount == -1)
        {
            Log.Info(this.getClass().getName() + " Success Discover");
            if (this.responseCount == -1) Log.Info(this.getClass().getName() + " No response, alone on the network");
            for (User user : this.userManager.getUsers()) Log.Info(this.getClass().getName() + " Discovered " + this.userManager.getUserIP(user.pseudo) + " --> " + user.pseudo + " / #" + user.id);

            return true;
        }

        Log.Error(this.getClass().getName() + " ERROR Discover timeout");
        this.userManager.reset();

        return false;
    }

    private boolean validUserCount()
    {
        boolean valid = true;

        try
        {
            semaphore.acquire();
            valid = this.responseCount < this.userCount;
            semaphore.release();
        }
        catch (InterruptedException e) { e.printStackTrace(); }

        return valid;
    }
}