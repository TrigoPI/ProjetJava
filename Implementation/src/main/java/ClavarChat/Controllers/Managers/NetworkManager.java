package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.Threads.*;
import ClavarChat.Models.Events.*;
import ClavarChat.Controllers.ClientHandler.ClientHandler;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Log.Log;

import java.util.HashMap;
import java.util.LinkedList;

public class NetworkManager implements Listener
{
    private int TCPPort;
    private int UDPPort;

    private EventManager eventManager;
    private NetworkThreadManager networkThreadManager;
    private TCPServerThread tcpServerThread;

    private HashMap<String, LinkedList<Paquet>> pendingDatas;
    private HashMap<String, ClientHandler> clients;

    public NetworkManager(int TCPPort, int UDPPort)
    {
        this.TCPPort = TCPPort;
        this.UDPPort = UDPPort;

        this.eventManager = EventManager.getInstance();
        this.networkThreadManager = new NetworkThreadManager();

        this.tcpServerThread = this.networkThreadManager.createTCPServerThread(TCPPort);

        this.pendingDatas = new HashMap<String, LinkedList<Paquet>>();
        this.clients = new HashMap<String, ClientHandler>();

        this.eventManager.addEvent(EVENT_TYPE.NETWORK_EVENT);
        this.eventManager.addListenner(this, EVENT_TYPE.NETWORK_EVENT);
    }

    public void startTCPServer()
    {
        this.tcpServerThread.start();
    }

    public void broadcast(Paquet paquet)
    {

    }

    public void sendTCP(Paquet paquet)
    {
        String ip = paquet.user.ip;

        if (!this.clients.containsKey(ip))
        {
            if (!this.pendingDatas.containsKey(ip)) this.connect(ip);
            this.pendingDatas.get(ip).push(paquet);
        }
        else
        {
            this.clients.get(ip).out.send(paquet);
        }
    }

    @Override
    public void onEvent(Event event)
    {
        Log.Print("NetworkManager Event --> " + event.type);

        switch (event.type)
        {
            case NETWORK_EVENT:
                this.onNetworkEvent((NetworkEvent)event);
                break;
        }
    }

    private void onNetworkEvent(NetworkEvent event)
    {
        switch (event.networkEventType)
        {
            case NETWORK_EVENT_DATA:
                Log.Print("NetworkManager Event --> " + event.networkEventType);
                this.onNetworkEventData((DataEvent)event);
                break;
            case NETWORK_EVENT_END_CONNECTION:
                Log.Print("NetworkEvent --> " + event.networkEventType);
                break;
            case NETWORK_EVENT_NEW_CONNECTION:
                this.onNewConnectionEvent((NewConnectionEvent)event);
                break;
        }
    }

    private void onNetworkEventData(DataEvent event)
    {
        Log.Info("new Paquet from : " + event.data.user.pseudo);
        this.eventManager.notiy(new PaquetEvent(event.data));
    }

    private void onNewConnectionEvent(NewConnectionEvent event)
    {
        Log.Info("New Connection : " + event.ip + ":" + event.port);
        Log.Print("Creating new IN/OUT socket with " + event.ip + ":" + event.port);

        TCPINSocketThread in = this.networkThreadManager.createTCPINSocketThread(event.socket);
        TCPOUTSocketThread out = this.networkThreadManager.createTCPOUTSocketThread(event.socket);

        this.clients.put(event.ip, new ClientHandler(in, out));
        if (this.pendingDatas.containsKey(event.ip)) this.flushPendingDatas(event.ip, out);

        in.start();
        out.start();
    }

    private void flushPendingDatas(String ip, TCPOUTSocketThread out)
    {
        LinkedList<Paquet> datas = this.pendingDatas.get(ip);
        while (!datas.isEmpty()) out.send(datas.pop());
        this.pendingDatas.remove(ip);
    }

    private void connect(String ip)
    {
        Log.Print("Trying to connect : " + ip + ":" + this.TCPPort);
        ConnectionThread connection = this.networkThreadManager.createConnectionThread(ip, this.TCPPort);
        connection.start();

        this.pendingDatas.put(ip, new LinkedList<Paquet>());
    }
}
