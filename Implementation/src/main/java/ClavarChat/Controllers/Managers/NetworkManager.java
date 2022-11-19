package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.Threads.*;
import ClavarChat.Models.Events.*;
import ClavarChat.Controllers.ClientHandler.ClientHandler;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Log.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;

public class NetworkManager implements Listener
{
    private int TCPPort;
    private int UDPPort;

    private EventManager eventManager;
    private NetworkThreadManager networkThreadManager;

    private TCPServerThread tcpServerThread;
    private UDPServerThread udpServerThread;

    private HashMap<String, LinkedList<Paquet>> pendingDatas;
    private HashMap<String, ClientHandler> clients;

    public NetworkManager(int TCPPort, int UDPPort)
    {
        this.TCPPort = TCPPort;
        this.UDPPort = UDPPort;

        this.eventManager = EventManager.getInstance();
        this.networkThreadManager = new NetworkThreadManager();

        this.tcpServerThread = this.networkThreadManager.createTCPServerThread(TCPPort);
        this.udpServerThread = this.networkThreadManager.createUDPServerThread(UDPPort);

        this.pendingDatas = new HashMap<String, LinkedList<Paquet>>();
        this.clients = new HashMap<String, ClientHandler>();

        this.eventManager.addEvent(EVENT_TYPE.NETWORK_EVENT);
        this.eventManager.addListenner(this, EVENT_TYPE.NETWORK_EVENT);
    }

    public void startTCPServer()
    {
        this.tcpServerThread.start();
    }

    public void startUDPServer()
    {
        this.udpServerThread.start();
    }

    public void sendUDP(Paquet paquet)
    {
        try
        {
            InetAddress addr = InetAddress.getByName(paquet.dst);
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(paquet);
            oo.close();

            byte[] serializedMessage = bStream.toByteArray();
            DatagramSocket datagramSocket = new DatagramSocket();
            DatagramPacket datagramPacket = new DatagramPacket(serializedMessage, serializedMessage.length, addr, this.UDPPort);
            datagramSocket.send(datagramPacket);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void sendTCP(Paquet paquet)
    {
        String ip = paquet.dst;

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

    public void closeTCP(String ip)
    {
        if (!this.clients.containsKey(ip))
        {
            Log.Warning(this.getClass().getName() + " no client with ip : " + ip);
        }
        else
        {
            this.clients.get(ip).stop();
            this.clients.remove(ip);
            Log.Info(this.getClass().getName() + " close TCP with : " + ip);
        }
    }

    @Override
    public void onEvent(Event event)
    {
        Log.Print(this.getClass().getName() + " Event --> " + event.type);

        switch (event.type)
        {
            case NETWORK_EVENT:
                this.onNetworkEvent((NetworkEvent)event);
                break;
        }
    }

    private void onNetworkEvent(NetworkEvent event)
    {
        Log.Print(this.getClass().getName() + " Event --> " + event.networkEventType);

        switch (event.networkEventType)
        {
            case NETWORK_EVENT_DATA:
                this.onNetworkDataEvent((DataEvent)event);
                break;
            case NETWORK_EVENT_CONNECTION:
                this.onConnectionEvent((EndConnectionEvent)event);
                break;
//            case NETWORK_EVENT_NEW_CONNECTION:
//                this.onNewConnectionEvent((SuccessConectionEvent)event);
//                break;
        }
    }

    private void onNetworkDataEvent(DataEvent event)
    {
        Log.Info(this.getClass().getName() + " new Paquet from : " + event.data.user.pseudo);
        Log.Info(this.getClass().getName() + " paquet type : " + event.data.type);
        System.out.println(event.data.user.pseudo + " : " + event.data.user.id);
        //this.eventManager.notiy(new PaquetEvent(event.data));
    }

    private void onConnectionEvent(ConnectionEvent event)
    {
        Log.Print(this.getClass().getName() + " Event --> " + event.connectionEventType);

        switch (event.connectionEventType)
        {
            case CONNECTION_EVENT_SUCCESS:
                this.onSuccessConnectionEvent((SuccessConectionEvent)event);
                break;
            case CONNECTION_EVENT_END:
                this.onEndConnectionEvent((EndConnectionEvent)event);
                break;
            case CONNECTION_EVENT_FAILED:
                break;
        }
    }

    private void onEndConnectionEvent(EndConnectionEvent event)
    {
        if (this.clients.containsKey(event.ip)) this.closeTCP(event.ip);
    }

    private void onSuccessConnectionEvent(SuccessConectionEvent event)
    {
        Log.Print(this.getClass().getName() + " creating new IN/OUT socket with " + event.ip + ":" + event.port);

        TCPINSocketThread in = this.networkThreadManager.createTCPINSocketThread(event.socket);
        TCPOUTSocketThread out = this.networkThreadManager.createTCPOUTSocketThread(event.socket);

        this.clients.put(event.ip, new ClientHandler(event.socket, in, out));
        if (this.pendingDatas.containsKey(event.ip)) this.flushPendingDatas(event.ip, out);

        in.start();
        out.start();
    }

    private void flushPendingDatas(String ip, TCPOUTSocketThread out)
    {
        Log.Print(this.getClass().getName() + " flushing data to THREAD_OUT : " + out.getIdString() + "-->" + out);
        LinkedList<Paquet> datas = this.pendingDatas.get(ip);
        while (!datas.isEmpty()) out.send(datas.removeLast());
        this.pendingDatas.remove(ip);
    }

    private void connect(String ip)
    {
        Log.Print(this.getClass().getName() + " trying to connect : " + ip + ":" + this.TCPPort);
        this.pendingDatas.put(ip, new LinkedList<Paquet>());
        ConnectionThread connection = this.networkThreadManager.createConnectionThread(ip, this.TCPPort);
        connection.start();
    }
}