package ClavarChat.Controllers.NetworkAPI;

import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.ThreadExecutable.Network.*;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.Events.ConnectionEvent;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.NetworkPaquetEvent;
import ClavarChat.Models.Events.SocketDataEvent;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Models.PackedArray.PackedArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class NetworkAPI implements Listener
{
    private final int tcpPort;
    private final int udpPort;

    private final EventManager eventManager;

    private final ThreadManager threadManager;
    private final NetworkManager networkManager;

    private final HashMap<String, Client> clients;

    private final int tcpServerID;
    private final int udpServerID;

    public NetworkAPI(ThreadManager threadManager, int tcpPort, int udpPort)
    {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;

        this.eventManager = EventManager.getInstance();

        this.networkManager = new NetworkManager();
        this.threadManager = threadManager;

        this.tcpServerID = this.networkManager.createTcpServer();
        this.udpServerID = this.networkManager.createUdpServer();

        this.clients = new HashMap<>();

        this.eventManager.addEvent(ConnectionEvent.CONNECTION_SUCCESS);
        this.eventManager.addEvent(ConnectionEvent.CONNECTION_FAILED);
        this.eventManager.addEvent(ConnectionEvent.CONNECTION_ENDED);
        this.eventManager.addEvent(SocketDataEvent.SOCKET_DATA);
        this.eventManager.addListenner(this, ConnectionEvent.CONNECTION_SUCCESS);
        this.eventManager.addListenner(this, ConnectionEvent.CONNECTION_FAILED);
        this.eventManager.addListenner(this, ConnectionEvent.CONNECTION_ENDED);
        this.eventManager.addListenner(this, SocketDataEvent.SOCKET_DATA);
    }

    public ArrayList<String> getBroadcastAddresses()
    {
        return this.networkManager.getBroadcastAddresses();
    }

    public void closeServer()
    {
        this.networkManager.closeUdpServer(this.udpServerID);
        this.networkManager.closeTcpServer(this.tcpServerID);
    }

    public void closeAll()
    {
//        for (String key : this.socketsId.keySet())
//        {
//            int socketId = this.socketsId.get(key);
//            Integer[] client = this.clientsMap.get(key);
//
//            TCPIN tcpin = this.tcpIn.get(client[0]);
//            TCPOUT tcpout = this.tcpOut.get(client[1]);
//
//            tcpin.stop();
//            tcpout.stop();
//
//            this.networkManager.closeTcpSocket(socketId);
//        }
//
//        this.socketsId.clear();
//        this.clientsMap.clear();
//        this.tcpIn.clear();
//        this.tcpOut.clear();
    }

    public void close(String ip)
    {
//        if (this.socketsId.containsKey(ip))
//        {
//            int socketId = this.socketsId.get(ip);
//            Integer[] client = this.clientsMap.get(ip);
//
//            TCPIN tcpin = this.tcpIn.get(client[0]);
//            TCPOUT tcpout = this.tcpOut.get(client[1]);
//
//            tcpin.stop();
//            tcpout.stop();
//
//            this.socketsId.remove(ip);
//            this.clientsMap.remove(ip);
//            this.tcpIn.remove(client[0]);
//            this.tcpOut.remove(client[1]);
//
//            this.networkManager.closeTcpSocket(socketId);
//        }
    }

    public void sendUDP(String ip, int port, ClavarChatMessage data)
    {
        this.networkManager.udpSend(data, ip, port);
    }

    public void sendTCP(String ip, int port, ClavarChatMessage data)
    {
        if (!this.clients.containsKey(ip))
        {
            int socketId = this.networkManager.createSocket();
            int threadId = this.threadManager.createThread();

            Client client = new Client(socketId);

            this.clients.put(ip, client);
            this.threadManager.setThreadRunnable(threadId, new TcpConnection(this.networkManager, socketId, ip, port));
            this.threadManager.startThread(threadId);
        }

        Client client = this.clients.get(ip);

        if (!client.connected)
        {
            client.pendingDatasBuffer.push(data);
        }
        else
        {
            client.out.put(data);
        }
    }

    public void startServer()
    {
        int tcpThreadID = this.threadManager.createThread(new TcpServer(this.networkManager, this.tcpServerID, this.tcpPort));
        int udpThreadID = this.threadManager.createThread(new UdpServer(this.networkManager, this.udpServerID, this.udpPort));

        this.threadManager.startThread(tcpThreadID);
        this.threadManager.startThread(udpThreadID);
    }

    @Override
    public void onEvent(Event event)
    {
        switch (event.type)
        {
            case ConnectionEvent.CONNECTION_SUCCESS:
                this.connectionSuccess((ConnectionEvent)event);
                break;
            case ConnectionEvent.CONNECTION_FAILED:
                this.connectionFailed((ConnectionEvent)event);
                break;
            case SocketDataEvent.SOCKET_DATA:
                this.onNetworkSocketDataEvent((SocketDataEvent)event);
                break;
        }
    }

    private void onNetworkSocketDataEvent(SocketDataEvent event)
    {
        ArrayList<String> ips = this.networkManager.getUserIp();

        if (!ips.contains(event.srcIp))
        {
            this.eventManager.notiy(new NetworkPaquetEvent(event.srcIp, event.srcPort, event.data));
        }
        else
        {
            Log.Print(this.getClass().getName() + " Dropping paquet from " + event.srcIp + ":" + event.srcPort);
        }
    }

    private void connectionSuccess(ConnectionEvent event)
    {
        int dstPort = event.dstPort;
        int srcPort = event.srcPort;

        String dstIp = event.dstIp;
        String srcIp = event.srcIp;

        Client client = this.clients.get(event.dstIp);

        int threadInId  = this.threadManager.createThread();
        int threadOutId = this.threadManager.createThread();

        TCPIN  in  = new TCPIN(this.networkManager, client.socketId);
        TCPOUT out = new TCPOUT(this.networkManager, client.socketId);

        client.srcIp = event.srcIp;
        client.dstIp = event.dstIp;

        client.srcPort = event.srcPort;
        client.dstPort = event.dstPort;

        client.in  = in;
        client.out = out;

        client.connected = true;

        this.threadManager.setThreadRunnable(threadInId, in);
        this.threadManager.setThreadRunnable(threadOutId, out);

        this.threadManager.startThread(threadInId);
        this.threadManager.startThread(threadOutId);

        Log.Print(this.getClass().getName() + " TCPIN  : " + srcIp + ":" + srcPort + " <-- " + dstIp + ":" + dstPort);
        Log.Print(this.getClass().getName() + " TCPOUT : " + srcIp + ":" + srcPort + " --> " + dstIp + ":" + dstPort);

        this.flushPendingData(client);
    }

    private void connectionFailed(ConnectionEvent event)
    {
        Log.Print(this.getClass().getName() + " Removing client : " + event.srcIp + ":" + event.srcPort + " --> " + event.dstIp + event.dstPort);
        this.clients.remove(event.dstIp);
    }

    private void flushPendingData(Client client)
    {
        if (client.pendingDatasBuffer.size() > 0)
        {
            Log.Print(this.getClass().getName() + " Flushing data to TCP Out : ");
            while (!client.pendingDatasBuffer.isEmpty()) client.out.put(client.pendingDatasBuffer.removeLast());
        }
    }

    private class Client {
        public boolean connected;

        public int socketId;

        public String srcIp;
        public String dstIp;

        public int srcPort;
        public int dstPort;

        public TCPIN in;
        public TCPOUT out;

        public LinkedList<Serializable> pendingDatasBuffer;

        public Client(int socketId)
        {
            this.socketId = socketId;

            this.connected = false;

            this.in = null;
            this.out = null;

            this.pendingDatasBuffer = new LinkedList<>();
        }
    }
}