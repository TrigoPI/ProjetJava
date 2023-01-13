package ClavarChat.Controllers.Managers.Thread;

import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.PackedArray.PackedArray;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadManager
{
    private final PackedArray<TMThread> threads;
    public ThreadManager()
    {
        this.threads = new PackedArray<>();
    }

    public int createThread()
    {
        TMThread thread = new TMThread(this);
        int threadId = this.threads.add(thread);

        thread.setId(threadId);
        Log.Print(this.getClass().getName() + " creating thread : " + threadId);

        return threadId;
    }

    public int createThread(TMRunnable runnable)
    {
        TMThread thread = new TMThread(this, runnable);
        int threadId = this.threads.add(thread);

        Log.Print(this.getClass().getName() + " creating thread : " + threadId + " --> " + runnable.getClass().getName());
        thread.setId(threadId);

        return threadId;
    }

    public void setRunnable(int threadId, TMRunnable runnable)
    {
        if (this.threads.get(threadId) == null)
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + threadId);
            return;
        }

        TMThread tmThread = this.threads.get(threadId);
        tmThread.setRunnable(runnable);
    }

    public void startThread(int threadId)
    {
        if (this.threads.get(threadId) == null)
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + threadId);
            return;
        }

        Log.Print(this.getClass().getName() + " starting thread : " + threadId);
        this.threads.get(threadId).start();

    }

    public void onFinished(int threadId)
    {
        if (this.threads.get(threadId) == null)
        {
            Log.Error(this.getClass().getName() + " no thread with id : " + threadId);
            return;
        }

        Log.Print(this.getClass().getName() + " removing thread : " + threadId);
        this.threads.remove(threadId);
    }

    private static class TMThread extends Thread
    {
        private int id;
        private final ThreadManager threadManager;
        private TMRunnable runnable;
        public TMThread(ThreadManager threadManager, TMRunnable runnable)
        {
            this.id = -1;
            this.threadManager = threadManager;
            this.runnable = runnable;
        }
        public TMThread(ThreadManager threadManager)
        {
            this.id = -1;
            this.threadManager = threadManager;
            this.runnable = null;
        }

        public void setId(int id)
        {
            this.id = id;
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
