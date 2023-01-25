package ClavarChat.Controllers.Handlers;

import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.API.NetworkAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcNetworkMessage.ClvcNetworkMessage;
import ClavarChat.Models.ClvcNetworkMessage.DiscoverResponseMessage;
import ClavarChat.Controllers.Managers.User.User;
import ClavarChat.Utils.Clock.Clock;
import ClavarChat.Utils.Log.Log;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscoverHandler implements MessageListener
{

    private final NetworkAPI networkAPI;
    private final DataBaseAPI dataBaseAPI;

    private final PseudoHandler pseudoHandler;
    private final UserManager userManager;

    private final int timeout;
    public final AtomicInteger numberOfUsers;
    public final AtomicInteger currentNumberOfUsers;

    public final AtomicInteger randomWait;

    public final AtomicBoolean running;
    public final AtomicBoolean finished;
    private final LinkedBlockingQueue<String> discoverQueue;
    private final LinkedBlockingQueue<String> otherUserQueue;

    public DiscoverHandler(NetworkAPI networkAPI, DataBaseAPI dataBaseAPI, UserManager userManager, PseudoHandler pseudoHandler)
    {
        this.networkAPI = networkAPI;
        this.dataBaseAPI = dataBaseAPI;
        this.userManager = userManager;
        this.pseudoHandler = pseudoHandler;
        this.randomWait = new AtomicInteger(0);
        this.numberOfUsers = new AtomicInteger(-1);
        this.currentNumberOfUsers = new AtomicInteger(0);
        this.finished = new AtomicBoolean(false);
        this.running = new AtomicBoolean(false);
        this.discoverQueue = new LinkedBlockingQueue<>();
        this.otherUserQueue = new LinkedBlockingQueue<>();
        this.timeout = 5;
    }

    public boolean discover()
    {
        this.waitResponse();

        if (!this.hasSucceed())
        {
            this.reset();
            return false;
        }

        this.flushUserQueue();
        this.reset();

        return this.pseudoHandler.checkPseudo();
    }

    @Override
    public void onData(String srcIp, ClvcNetworkMessage message)
    {
        switch (message.type)
        {
            case ClvcNetworkMessage.WAIT :
                this.onWait(srcIp);
                break;
            case ClvcNetworkMessage.WAIT_FINISHED :
                this.onWaitFinished(srcIp);
                break;
            case DiscoverResponseMessage.DISCOVER_RESPONSE :
                this.onDiscoverResponse((DiscoverResponseMessage)message, srcIp);
                break;
            case ClvcNetworkMessage.DISCOVER_REQUEST :
                this.onDiscoverRequest(srcIp);
                break;
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

    private void onWaitFinished(String srcIp)
    {
        Log.Info(DiscoverHandler.class.getName() + " " + srcIp + " finished his discovering, removing to queue");
        this.discoverQueue.remove(srcIp);

        if (this.discoverQueue.isEmpty())
        {
            this.numberOfUsers.set(-1);
            this.currentNumberOfUsers.set(0);
            this.networkAPI.sendDiscoverRequest();
        }
    }

    private void onWait(String srcIp)
    {
        Log.Info(DiscoverHandler.class.getName() + " Waiting for " + srcIp + " to finished his discovering, adding to queue");
        if (this.discoverQueue.contains(srcIp)) return;

        this.discoverQueue.add(srcIp);

        int t = new Random().nextInt(5, 15);
        this.randomWait.set(t);
        System.out.println("eziezojirtzj --> " + t);
    }

    private void onDiscoverRequest(String srcIp)
    {
        Log.Print(this.getClass().getName() + " Discover from : " + srcIp);

        if (this.running.get())
        {
            Log.Info(this.getClass().getName() + " Discover from : " + srcIp + ", adding to queue");
            if (!this.discoverQueue.contains(srcIp)) this.otherUserQueue.add(srcIp);
            this.networkAPI.sendWait(srcIp);
        }

        this.networkAPI.sendDiscoverResponse(srcIp);
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

        if (this.currentNumberOfUsers.get() == this.numberOfUsers.get()) this.finished.set(true);
    }

    private void waitResponse()
    {
        Log.Info(DiscoverHandler.class.getName() + " Waiting for response");
        Clock clock1 = new Clock();
        Clock clock2 = new Clock();

        this.running.set(true);
        this.networkAPI.sendDiscoverRequest();

        while (clock1.timeSecond() < timeout && !this.finished.get())
        {
            if (!this.discoverQueue.isEmpty())
            {
                clock1.resetSecond();

                if (clock2.timeSecond() > 10 + this.randomWait.get())
                {
                    Log.Warning(DiscoverHandler.class.getName() + " No response in " + (10 + this.randomWait.get()) + ", reset timer");

                    this.discoverQueue.clear();
                    this.otherUserQueue.clear();
                    this.numberOfUsers.set((-1));
                    this.currentNumberOfUsers.set(0);
                    this.networkAPI.sendDiscoverRequest();
                }
            }
            else
            {
                clock2.resetSecond();
            }
        }
    }

    private void flushUserQueue()
    {
        while (!this.otherUserQueue.isEmpty()) this.networkAPI.sendWaitFinished(this.otherUserQueue.poll());
    }

    private void reset()
    {
        this.running.set(false);
        this.finished.set(false);
        this.numberOfUsers.set(-1);
        this.currentNumberOfUsers.set(0);
    }
}
