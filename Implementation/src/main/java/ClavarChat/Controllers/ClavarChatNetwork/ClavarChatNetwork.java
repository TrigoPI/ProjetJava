package ClavarChat.Controllers.ClavarChatNetwork;

import ClavarChat.Controllers.ClavarChatNetwork.Runnable.TcpConnection;
import ClavarChat.Controllers.ClavarChatNetwork.Runnable.TcpServer;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Controllers.Managers.ThreadManager;
import ClavarChat.Models.Events.Event;
import ClavarChat.Utils.Log.Log;

public class ClavarChatNetwork implements Listener
{
    private EventManager eventManager;

    private ThreadManager threadManager;
    private NetworkManager networkManager;

    private int tcpServerID;
    private int udpServerID;

    public ClavarChatNetwork(ThreadManager threadManager, NetworkManager networkManager)
    {
        this.eventManager = EventManager.getInstance();

        this.threadManager = threadManager;
        this.networkManager = networkManager;

        this.tcpServerID = this.threadManager.createThread();
        this.udpServerID = this.threadManager.createThread();

        this.eventManager.addListenner(this, Event.EVENT_TYPE.EVENT_NETWORK_CONNECTION);
    }

    public void startServer()
    {
        this.threadManager.setThreadRunnable(tcpServerID, new TcpServer(this.networkManager, 8080));
        this.threadManager.startThread(this.tcpServerID);
    }

    @Override
    public void onEvent(Event event)
    {
        Log.Error("OOOOOOOOOOOOOOOOOOOOOOOOOOOOK");

        switch (event.type)
        {
            case EVENT_NETWORK_CONNECTION:
                break;
        }
    }
}
