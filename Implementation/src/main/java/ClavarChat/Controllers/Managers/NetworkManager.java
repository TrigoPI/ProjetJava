package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.Threads.*;
import ClavarChat.Models.Events.*;
import ClavarChat.Controllers.ClientHandler.ClientHandler;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Loggin.Loggin;

import java.util.HashMap;

public class NetworkManager implements Listener
{
    private EventManager eventManager;
    private NetworkThreadManager networkThreadManager;
    private TCPServerThread tcpServerThread;

    private HashMap<String, ClientHandler> clients;

    public NetworkManager()
    {
        this.eventManager = EventManager.getInstance();
        this.networkThreadManager = new NetworkThreadManager();
        this.tcpServerThread = this.networkThreadManager.createTCPServerThread(4000);

        this.clients = new HashMap<String, ClientHandler>();

        this.eventManager.addEvent(EVENT_TYPE.NETWORK_EVENT);
        this.eventManager.addListenner(this, EVENT_TYPE.NETWORK_EVENT);
    }

    public void broadcast(Paquet paquet)
    {

    }

    public void sendTCP(Paquet paquet)
    {
        String ip = paquet.user.ip;

        if (this.clients.containsKey(ip))
        {
            this.clients.get(ip).out.send(paquet);
        }
        else
        {
            Loggin.Print("Creating new OUT socket with " + ip);

            TCPOUTSocketThread out = this.networkThreadManager.TCPOUTSocketThread(ip, 4000);
            ClientHandler client = new ClientHandler();

            this.clients.put(ip, client);

            client.setOut(out);
            client.out.send(paquet);
        }
    }

    @Override
    public void onEvent(Event event)
    {
        Loggin.Print("NetworkManager Event --> " + event.type);

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
                Loggin.Print("NetworkManager Event --> " + event.networkEventType);
                this.onNetworkEventData((DataEvent)event);
                break;
            case NETWORK_EVENT_CONNECTION_SUCCESS:
                Loggin.Print("NetworkManager Event --> " + event.networkEventType);
                this.onConnectionSuccessEvent((ConnectionSuccessEvent)event);
                break;
            case NETWORK_EVENT_END_CONNECTION:
                break;
            case NETWORK_EVENT_NEW_CONNECTION:
                break;
        }
    }

    private void onNetworkEventData(DataEvent event)
    {
        this.eventManager.notiy(new PaquetEvent(event.data));
    }

    private void onConnectionSuccessEvent(ConnectionSuccessEvent event)
    {
        Loggin.Print("Creating new IN socket with " + event.ip);
        TCPINSocketThread in = this.networkThreadManager.createTCPINSocketThread(event.socket);
        this.clients.get(event.ip).setIn(in);
    }

    private void onNewConnectionEvent(NewConnectionEvent event)
    {
        Loggin.Info("New Connection : " + event.ip);
        Loggin.Print("Creating new IN/OUT socket with " + event.ip);

        TCPINSocketThread in = this.networkThreadManager.createTCPINSocketThread(event.socket);
        TCPOUTSocketThread out = this.networkThreadManager.createTCPOUTSocketThread(event.socket);

        this.clients.put(event.ip, new ClientHandler(in, out));
    }
}
