package ClavarChat.Controllers.Modules;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.ClavarChatMessage.Enums.MESSAGE_TYPE;
import ClavarChat.Models.ClavarChatMessage.DiscoverInformationMessage;
import ClavarChat.Utils.Clock.Clock;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class DiscoverModule
{
    private NetworkManager networkManager;
    private Semaphore semaphore;

    private int userCount;
    private int responseCount;
    private int timeout;

    private boolean success;

    public DiscoverModule(NetworkManager networkManager)
    {
        this.networkManager = networkManager;
        this.semaphore = new Semaphore(1);

        this.userCount = 0;
        this.responseCount = -1;
        this.timeout = 10;

        this.success = false;
    }

    public void discover()
    {
        this.broadcast();
        this.waitResponses();
    }

    public void onDiscoverInformation(DiscoverInformationMessage data)
    {
        try
        {
            semaphore.acquire();

            if (this.responseCount == -1)
            {
                Log.Print(this.getClass().getName() + " Waiting for " + data.userCount + " responses");
                this.responseCount = 0;
                this.userCount = data.userCount;
            }

            this.responseCount++;

            semaphore.release();
        }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    public boolean succeed()
    {
        return this.success;
    }

    private void broadcast()
    {
        ArrayList<String> broadcast = this.networkManager.getBroadcastAddresses();
        for (String addresse : broadcast) this.networkManager.sendUDP(new ClavarChatMessage(MESSAGE_TYPE.DISCOVER), addresse);
    }

    private void waitResponses()
    {
        Log.Print(this.getClass().getName() + "Waiting...");

        Clock clock = new Clock();

        while (this.isValid() && clock.timeSecond() < this.timeout) {}

        if (this.responseCount == this.userCount || this.responseCount == -1)
        {
            Log.Info(this.getClass().getName() + " Success Discover");
            if (this.responseCount == -1) Log.Info(this.getClass().getName() + " No response, alone on the network");
            this.success = true;
        }
        else
        {
            Log.Error(this.getClass().getName() + " ERROR Discover timeout");
            this.success = false;
        }
    }

    private boolean isValid()
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
