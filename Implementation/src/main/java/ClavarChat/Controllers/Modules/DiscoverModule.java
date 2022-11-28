package ClavarChat.Controllers.Modules;

import ClavarChat.Controllers.ClavarChatNetwork.ClavarChatNetwork;
import ClavarChat.Controllers.Managers.UserManager;
import ClavarChat.Models.ClavarChatMessage.DiscoverMessage;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Clock.Clock;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class DiscoverModule extends Handler
{
    private int udpPort;

    private ClavarChatNetwork clavarChatNetwork;
    private UserManager userManager;
    private Semaphore semaphore;

    private int userCount;
    private int responseCount;
    private int timeout;

    public DiscoverModule(ClavarChatNetwork clavarChatNetwork, UserManager userManager, int udpPort)
    {
        super();

        this.udpPort = udpPort;

        this.clavarChatNetwork = clavarChatNetwork;
        this.userManager = userManager;
        this.semaphore = new Semaphore(1);

        this.userCount = 0;
        this.responseCount = -1;
        this.timeout = 10;
    }

    public void onDiscoverInformation(DiscoverMessage data, String src)
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

            this.userManager.addUser(data.user, src);
            this.responseCount++;

            semaphore.release();
        }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    public void handle()
    {
        this.broadcast();
        this.waitResponses();
        if (this.succeed() && this.next != null) this.next.handle();
    }

    private void broadcast()
    {
        ArrayList<String> broadcast = this.clavarChatNetwork.getBroadcastAddresses();
        for (String addresse : broadcast) this.clavarChatNetwork.sendUDP(addresse, this.udpPort, new DiscoverMessage());
    }

    private void waitResponses()
    {
        Log.Info(this.getClass().getName() + " Waiting...");
        Clock clock = new Clock();
        while (this.validUserCount() && clock.timeSecond() < this.timeout) {}
    }

    private boolean succeed()
    {
        if (this.responseCount == this.userCount || this.responseCount == -1)
        {
            Log.Info(this.getClass().getName() + " Success Discover");
            if (this.responseCount == -1) Log.Info(this.getClass().getName() + " No response, alone on the network");
            for (UserData user : this.userManager.getUsers()) Log.Info(this.getClass().getName() + " Discovered " + this.userManager.getUserIP(user.pseudo) + " --> " + user.pseudo + " / #" + user.id);

            return true;
        }

        Log.Error(this.getClass().getName() + " ERROR Discover timeout");

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