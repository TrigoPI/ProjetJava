package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Models.Events.Event.EVENT_TYPE;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;
import ClavarChat.Models.Events.ThreadEvent;
import ClavarChat.Models.Events.Event;
import ClavarChat.Controllers.Threads.*;
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.Log.Log;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ThreadManager implements Listener
{
    private EventManager eventManager;
    private HashMap<Integer, TMThread> threads;
    private int currentID;

    public ThreadManager()
    {
        this.currentID = 0;
        this.eventManager = EventManager.getInstance();
        this.threads = new HashMap<Integer, TMThread>();

        this.eventManager.addEvent(EVENT_TYPE.EVENT_THREAD);
        this.eventManager.addListenner(this, EVENT_TYPE.EVENT_THREAD);

        this.DEBUG();
    }

    private void DEBUG()
    {
        ModuleCLI moduleCLI = new ModuleCLI();

        moduleCLI.addCommand("threads", () -> {
            for (Integer key : this.threads.keySet()) System.out.println(key + " --> " + this.threads.get(key) + "/" + this.threads.get(key).getClass().getName());
        });

        CLI.installModule("thread", moduleCLI);
    }

    public int createThread()
    {
        Log.Print(this.getClass().getName() + " creating thread : " + this.currentID);
        this.threads.put(this.currentID, new TMThread(this.currentID));
        return currentID++;
    }

    public int createThread(ThreadRunnable runnable)
    {
        Log.Print(this.getClass().getName() + " creating thread : " + this.currentID + " --> " + runnable.getClass().getName());
        this.threads.put(this.currentID, new TMThread(runnable, this.currentID));
        return currentID++;
    }

    public void removeThread(int id)
    {
        if (this.threads.containsKey(id))
        {
            Log.Print(this.getClass().getName() + " removing thread : " + id);
            this.threads.remove(id);
        }
        else
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + id);
        }
    }

    public void setThreadRunnable(int id, ThreadRunnable runnable)
    {
        if (this.threads.containsKey(id))
        {
            this.threads.get(id).setRunnable(runnable);
        }
        else
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + id);
        }
    }

    public void startThread(int id)
    {
        if (this.threads.containsKey(id))
        {
            Log.Print(this.getClass().getName() + " starting thread : " + id);
            this.threads.get(id).start();
        }
        else
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + id);
        }
    }

    @Override
    public void onEvent(Event event)
    {
        switch (event.type)
        {
            case EVENT_THREAD:
                this.onThreadEvent((ThreadEvent)event);
                break;
        }
    }

    private void onThreadEvent(ThreadEvent event)
    {
        switch (event.status)
        {
            case FINISHED:
                this.onThreadFinished(event.id);
                break;
        }
    }

    private void onThreadFinished(int id)
    {
        this.removeThread(id);
    }
}