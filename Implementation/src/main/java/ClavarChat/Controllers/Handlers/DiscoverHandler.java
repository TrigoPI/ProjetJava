package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.API.NetworkAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcNetworkMessage.ClvcNetworkMessage;
import ClavarChat.Models.ClvcNetworkMessage.DiscoverRequestMessage;
import ClavarChat.Models.ClvcNetworkMessage.DiscoverResponseMessage;
import ClavarChat.Models.ClvcNetworkMessage.WaitMessage;
import ClavarChat.Models.ClvcNetworkMessage.LoginMessage;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Clock.Clock;
import ClavarChat.Utils.Log.Log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscoverHandler implements MessageListener
{
    private final NetworkAPI networkAPI;
    private final DataBaseAPI dataBaseAPI;

    private final PseudoHandler pseudoHandler;
    private final UserManager userManager;


    private final int timeout;

    public final AtomicInteger numberOfDiscover;
    public final AtomicInteger numberOfUsers;
    public final AtomicInteger currentNumberOfUsers;
    public final AtomicBoolean finished;

    public DiscoverHandler(NetworkAPI networkAPI, DataBaseAPI dataBaseAPI, UserManager userManager, PseudoHandler pseudoHandler)
    {
        this.networkAPI = networkAPI;
        this.dataBaseAPI = dataBaseAPI;
        this.userManager = userManager;
        this.pseudoHandler = pseudoHandler;
        this.numberOfUsers = new AtomicInteger(-1);
        this.currentNumberOfUsers = new AtomicInteger(0);
        this.numberOfDiscover = new AtomicInteger(0);
        this.finished = new AtomicBoolean(false);
        this.timeout = 2;
    }

    public boolean discover()
    {
        this.waitResponse();

        if (!this.hasSucceed())
        {
            this.reset();
            return false;
        }

        this.reset();
        return this.pseudoHandler.checkPseudo();
    }

    @Override
    public void onData(String srcIp, ClvcNetworkMessage message)
    {
        switch (message.type)
        {
            case LoginMessage.LOGIN -> this.onLogin();
            case WaitMessage.WAIT -> this.onWait();
            case DiscoverResponseMessage.DISCOVER_RESPONSE -> this.onDiscoverResponse((DiscoverResponseMessage)message, srcIp);
            case DiscoverRequestMessage.DISCOVER_REQUEST -> this.onDiscoverRequest(srcIp);
        }
    }

    private boolean hasSucceed()
    {
        if (this.numberOfUsers.get() == -1)
        {
            Log.Info(DiscoverHandler.class.getName() + " Success alone on network");
            return true;
        }

        if (this.numberOfUsers.get() != this.currentNumberOfUsers.get())
        {
            Log.Error(DiscoverHandler.class.getName() + " ERROR missing response");
            return false;
        }

        Log.Info(DiscoverHandler.class.getName() + " Success Discover");
        for (User user : this.userManager.getUsers()) Log.Info(DiscoverHandler.class.getName() + " Discovered : " + user.pseudo + "/#" + user.id);
        return true;
    }

    private void onLogin()
    {
        this.numberOfDiscover.decrementAndGet();
    }

    private void onWait()
    {
        this.numberOfDiscover.incrementAndGet();
    }

    private void onDiscoverRequest(String dstIp)
    {
        Log.Print(this.getClass().getName() + " Discover from : " + dstIp);
        this.networkAPI.sendWait(dstIp);
        this.networkAPI.sendDiscoverResponse(dstIp);
    }

    private void onDiscoverResponse(DiscoverResponseMessage data, String dstIp)
    {
        Log.Info(this.getClass().getName() + " Discover information from user : " + data.pseudo + " / " + "#" + data.id);

        this.dataBaseAPI.addUser(data.id, data.pseudo, data.avatar);
        this.userManager.addUser(data.pseudo, data.id, data.avatar);
        this.userManager.addIpToUser(data.id, dstIp);
        this.updateUserCount(data);
    }

    public void updateUserCount(DiscoverResponseMessage data)
    {
        this.currentNumberOfUsers.incrementAndGet();

        if (this.numberOfUsers.get() == -1)
        {
            Log.Info(this.getClass().getName() + " Waiting for : " + data.count);
            this.numberOfUsers.set(data.count);
        }

        if (this.currentNumberOfUsers.get() == this.numberOfUsers.get())
        {
            this.finished.set(true);
        }
    }

    private void waitResponse()
    {
        Log.Info(DiscoverHandler.class.getName() + " Waiting for response");
        Clock clock = new Clock();
        this.networkAPI.sendDiscoverRequest();
        while (clock.timeSecond() < timeout && this.numberOfDiscover.get() == 0 && !this.finished.get());
    }

    private void reset()
    {
        this.finished.set(false);
        this.numberOfUsers.set(-1);
        this.currentNumberOfUsers.set(0);
    }
}
