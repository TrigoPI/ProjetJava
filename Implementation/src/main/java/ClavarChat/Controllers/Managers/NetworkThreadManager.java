package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Threads.NetworkThread;
import ClavarChat.Controllers.Threads.TCPINSocketThread;
import ClavarChat.Controllers.Threads.TCPOUTSocketThread;
import ClavarChat.Controllers.Threads.TCPServerThread;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Events.Event;
import ClavarChat.Utils.Log.Log;

import java.net.Socket;
import java.net.UnknownHostException;
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

    public TCPOUTSocketThread createTCPOUTSocketThread(Socket socket)
    {
        TCPOUTSocketThread thread = new TCPOUTSocketThread(socket);
        this.threads.put(thread.getIdString(), thread);
        Log.Print("New TCP_OUT thread " + thread.getIdString() + " --> " + thread.toString());
        thread.start();
        return thread;
    }

    public TCPOUTSocketThread TCPOUTSocketThread(String ip, int port)
    {
        try
        {
            TCPOUTSocketThread thread = new TCPOUTSocketThread(ip, port);
            this.threads.put(thread.getIdString(), thread);
            return thread;
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public TCPINSocketThread createTCPINSocketThread(Socket socket)
    {
        TCPINSocketThread thread = new TCPINSocketThread(socket);
        this.threads.put(thread.getIdString(), thread);
        return thread;
    }

    public TCPServerThread createTCPServerThread(int port)
    {
        TCPServerThread thread = new TCPServerThread(port);
        this.threads.put(thread.getIdString(), thread);
        return thread;
    }

    @Override
    public void onEvent(Event event)
    {

    }
}
