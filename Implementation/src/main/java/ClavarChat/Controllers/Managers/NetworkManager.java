package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.Threads.*;
import ClavarChat.Models.Events.*;
import ClavarChat.Controllers.ClientHandler.ClientHandler;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Log.Log;

import java.util.HashMap;

public class NetworkManager implements Listener
{
    private int TCPPort;
    private int UDPPort;

    private EventManager eventManager;
    private NetworkThreadManager networkThreadManager;
    private TCPServerThread tcpServerThread;

    private HashMap<String, ClientHandler> clients;

    public NetworkManager(int TCPPort, int UDPPort)
    {
        this.TCPPort = TCPPort;
        this.UDPPort = UDPPort;

        this.eventManager = EventManager.getInstance();
        this.networkThreadManager = new NetworkThreadManager();

        this.tcpServerThread = this.networkThreadManager.createTCPServerThread(TCPPort);

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
        if (!this.clients.containsKey(ip)) this.createOutSocket(ip);
        this.clients.get(ip).out.send(paquet);
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
            case NETWORK_EVENT_CONNECTION_SUCCESS:
                Log.Print("NetworkManager Event --> " + event.networkEventType);
                this.onConnectionSuccessEvent((ConnectionSuccessEvent)event);
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
        //this.eventManager.notiy(new PaquetEvent(event.data));
    }

    private void onConnectionSuccessEvent(ConnectionSuccessEvent event)
    {
        Log.Print("Creating new IN socket with " + event.ip + ":" + event.port);
        TCPINSocketThread in = this.networkThreadManager.createTCPINSocketThread(event.socket);

        this.clients.get(event.ip).setIn(in);

        in.start();
    }

    private void onNewConnectionEvent(NewConnectionEvent event)
    {
        Log.Info("New Connection : " + event.ip + ":" + event.port);
        Log.Print("Creating new IN/OUT socket with " + event.ip + ":" + event.port);

        TCPINSocketThread in = this.networkThreadManager.createTCPINSocketThread(event.socket);
        TCPOUTSocketThread out = this.networkThreadManager.createTCPOUTSocketThread(event.socket);

        this.clients.put(event.ip, new ClientHandler(in, out));

        in.start();
        out.start();
    }

    private void createOutSocket(String ip)
    {
        Log.Print("Creating new OUT socket with " + ip + ":" + this.TCPPort);
        TCPOUTSocketThread out = this.networkThreadManager.TCPOUTSocketThread(ip, 4000);
        ClientHandler client = new ClientHandler();

        this.clients.put(ip, client);

        client.setOut(out);
        out.start();
    }
}
