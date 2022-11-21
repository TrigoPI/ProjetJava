package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Threads.*;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.ThreadEvent;
import ClavarChat.Utils.Log.Log;

import java.net.Socket;
import java.util.HashMap;

public class NetworkThreadManager implements Listener
{
    private EventManager eventManager;
    private HashMap<String, NetworkThread> threads;

    public NetworkThreadManager()
    {
        this.eventManager = EventManager.getInstance();
        this.threads = new HashMap<String, NetworkThread>();

        this.eventManager.addEvent(EVENT_TYPE.THREAD_EVENT);
        this.eventManager.addListenner(this, EVENT_TYPE.THREAD_EVENT);
    }

    public void removeThread(String id)
    {
        if (!this.threads.containsKey(id))
        {
            Log.Warning(this.getClass().getName() + " no thread with id : " + id);
        }
        else
        {
            Log.Print(this.getClass().getName() + " removing thread : " + id);
            this.threads.remove(id);
        }
    }

    public ConnectionThread createConnectionThread(String ip, int port)
    {
        ConnectionThread thread = new ConnectionThread(ip, port);
        this.threads.put(thread.getIdString(), thread);
        Log.Print(this.getClass().getName() + " new Connection thread " + thread.getIdString() + " --> " + thread.toString());
        return thread;
    }

    public TCPOUTSocketThread createTCPOUTSocketThread(Socket socket)
    {
        TCPOUTSocketThread thread = new TCPOUTSocketThread(socket);
        this.threads.put(thread.getIdString(), thread);
        Log.Print(this.getClass().getName() + " new TCP_OUT thread " + thread.getIdString() + " --> " + thread.toString());
        return thread;
    }

    public TCPINSocketThread createTCPINSocketThread(Socket socket)
    {
        TCPINSocketThread thread = new TCPINSocketThread(socket);
        this.threads.put(thread.getIdString(), thread);
        Log.Print(this.getClass().getName() + " new TCP_IN thread " + thread.getIdString() + " --> " + thread.toString());
        return thread;
    }

    public UDPServerThread createUDPServerThread(int port)
    {
        UDPServerThread thread = new UDPServerThread(port);
        this.threads.put(thread.getIdString(), thread);
        Log.Print(this.getClass().getName() + " new UDP Server thread " + thread.getIdString() + " --> " + thread.toString());
        return thread;
    }

    public TCPServerThread createTCPServerThread(int port)
    {
        TCPServerThread thread = new TCPServerThread(port);
        this.threads.put(thread.getIdString(), thread);
        Log.Print(this.getClass().getName() + " new TCP Server thread " + thread.getIdString() + " --> " + thread.toString());
        return thread;
    }

    @Override
    public void onEvent(Event event)
    {
        //                Log.Print(this.getClass().getName() + " Event --> " + event.type);

        switch (event.type)
        {
            case THREAD_EVENT:
                this.onThreadEvent((ThreadEvent)event);
                break;
        }
    }

    private void onThreadEvent(ThreadEvent event)
    {
//        Log.Print(this.getClass().getName() + " Event --> " + event.threadEventType);

        switch (event.threadEventType)
        {
            case THREAD_EVENT_FINISHED:
                this.onThreadFinishedEvent(event);
                break;
        }
    }

    private void onThreadFinishedEvent(ThreadEvent event)
    {
        this.removeThread(event.threadID);
    }
}
