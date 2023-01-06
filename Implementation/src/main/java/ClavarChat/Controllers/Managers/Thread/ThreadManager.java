package ClavarChat.Controllers.Managers.Thread;

import ClavarChat.Utils.Log.Log;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadManager
{
    private final HashMap<Integer, TMThread> threads;
    private int currentID;

    public ThreadManager()
    {
        this.currentID = 0;
        this.threads = new HashMap<>();
    }

    public int createThread()
    {
        Log.Print(this.getClass().getName() + " creating thread : " + this.currentID);
        this.threads.put(this.currentID, new TMThread(this, this.currentID));
        return currentID++;
    }

    public int createThread(TMRunnable runnable)
    {
        Log.Print(this.getClass().getName() + " creating thread : " + this.currentID + " --> " + runnable.getClass().getName());
        this.threads.put(this.currentID, new TMThread(this, runnable, this.currentID));
        return currentID++;
    }

    public void setRunnable(int threadId, TMRunnable runnable)
    {
        if (!this.threads.containsKey(threadId))
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + threadId);
            return;
        }

        TMThread tmThread = this.threads.get(threadId);
        tmThread.setRunnable(runnable);
    }

    public void removeThread(int threadId)
    {
        if (!this.threads.containsKey(threadId))
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + threadId);
            return;
        }

        Log.Print(this.getClass().getName() + " removing thread : " + threadId);
        this.threads.remove(threadId);
    }

    public void startThread(int threadId)
    {
        if (!this.threads.containsKey(threadId))
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + threadId);
            return;
        }

        Log.Print(this.getClass().getName() + " starting thread : " + threadId);
        this.threads.get(threadId).start();

    }

    public void onFinished(int threadId)
    {
        this.removeThread(threadId);
    }

    private static class TMThread extends Thread
    {
        private final int id;
        private final ThreadManager threadManager;
        private TMRunnable runnable;
        public TMThread(ThreadManager threadManager, TMRunnable runnable, int id)
        {
            this.id = id;
            this.threadManager = threadManager;
            this.runnable = runnable;
        }
        public TMThread(ThreadManager threadManager, int id)
        {
            this.id = id;
            this.threadManager = threadManager;
            this.runnable = null;
        }

        public void setRunnable(TMRunnable runnable)
        {
            if (this.runnable != null)
            {
                Log.Warning(this.getClass().getName() +  " Runnable already set");
                return;
            }

            this.runnable = runnable;
        }

        @Override
        public void run()
        {
            if (this.runnable == null)
            {
                Log.Warning(this.getClass().getName() +  " Runnable is null");
                return;
            }

            this.runnable.run();
            this.threadManager.onFinished(this.id);
        }
    }
}
