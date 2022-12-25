package ClavarChat.Controllers.Managers.Thread;

import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Models.Events.Thread.ThreadEvent;
import ClavarChat.Models.Events.Event;
import ClavarChat.Utils.Log.Log;

import java.util.HashMap;

public class ThreadManager implements Listener
{
    private final EventManager eventManager;
    private final HashMap<Integer, TMThread> threads;
    private int currentID;

    public ThreadManager()
    {
        this.currentID = 0;
        this.eventManager = EventManager.getInstance();
        this.threads = new HashMap<>();

        this.eventManager.addEvent(ThreadEvent.THREAD_FINISHED);
        this.eventManager.addListener(this, ThreadEvent.THREAD_FINISHED);
    }

    public int createThread()
    {
        Log.Print(this.getClass().getName() + " creating thread : " + this.currentID);
        this.threads.put(this.currentID, new TMThread(this.currentID));
        return currentID++;
    }

    public int createThread(ThreadExecutable runnable)
    {
        Log.Print(this.getClass().getName() + " creating thread : " + this.currentID + " --> " + runnable.getClass().getName());
        this.threads.put(this.currentID, new TMThread(runnable, this.currentID));
        return currentID++;
    }

    public void removeThread(int threadId)
    {
        if (this.threads.containsKey(threadId))
        {
            Log.Print(this.getClass().getName() + " removing thread : " + threadId);
            this.threads.remove(threadId);
        }
        else
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + threadId);
        }
    }

    public void setThreadRunnable(int threadId, ThreadExecutable runnable)
    {
        if (this.threads.containsKey(threadId))
        {
            Log.Print(this.getClass().getName() + " Adding " + runnable.getClass().getName() + " to thread : " + threadId);
            this.threads.get(threadId).setExecutable(runnable);
        }
        else
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + threadId);
        }
    }

    public void startThread(int threadId)
    {
        if (this.threads.containsKey(threadId))
        {
            Log.Print(this.getClass().getName() + " starting thread : " + threadId);
            this.threads.get(threadId).start();
        }
        else
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + threadId);
        }
    }

    @Override
    public void onEvent(Event event)
    {
        switch (event.type)
        {
            case ThreadEvent.THREAD_FINISHED:
                this.onThreadFinished((ThreadEvent)event);
                break;
        }
    }

    private void onThreadFinished(ThreadEvent event)
    {
        this.removeThread(event.id);
    }
}
