package ClavarChat.Controllers.NetworkAPI;

import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.ThreadExecutable.Network.Connection.TcpConnection;
import ClavarChat.Controllers.ThreadExecutable.Network.Messagin.TCPIN;
import ClavarChat.Controllers.ThreadExecutable.Network.Messagin.TCPOUT;
import ClavarChat.Controllers.ThreadExecutable.Network.Server.TcpServer;
import ClavarChat.Controllers.ThreadExecutable.Network.Server.UdpServer;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.Events.Network.ConnectionEvent;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.Network.NetworkPacketEvent;
import ClavarChat.Models.Events.Network.SocketDataEvent;
import ClavarChat.Models.Events.Network.SocketSendingEndEvent;
import ClavarChat.Utils.Log.Log;

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

        this.eventManager.addEvent(SocketSendingEndEvent.FINISHED_SENDING);
        this.eventManager.addEvent(ConnectionEvent.CONNECTION_SUCCESS);
        this.eventManager.addEvent(ConnectionEvent.CONNECTION_FAILED);
        this.eventManager.addEvent(ConnectionEvent.CONNECTION_ENDED);
        this.eventManager.addEvent(ConnectionEvent.CONNECTION_NEW);
        this.eventManager.addEvent(SocketDataEvent.SOCKET_DATA);

        this.eventManager.addListenner(this, SocketSendingEndEvent.FINISHED_SENDING);
        this.eventManager.addListenner(this, ConnectionEvent.CONNECTION_SUCCESS);
        this.eventManager.addListenner(this, ConnectionEvent.CONNECTION_FAILED);
        this.eventManager.addListenner(this, ConnectionEvent.CONNECTION_ENDED);
        this.eventManager.addListenner(this, ConnectionEvent.CONNECTION_NEW);
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

    public void closeAllClients()
    {
        for (String key : this.clients.keySet())
        {
            Log.Info(this.getClass().getName() + " Closing " + key);

            Client client = this.clients.get(key);

            if (client.status == Client.STATUS.CONNECTING)
            {
                Log.Info(this.getClass().getName() + " Cannot close : " + key + " because socket is connecting --> CLOSE_WAIT");
                client.status = Client.STATUS.CLOSE_WAIT;
            }

            if (client.status == Client.STATUS.CONNECTED)
            {
                if (client.isSending)
                {
                    Log.Info(this.getClass().getName() + " Cannot close : " + key + " because socket is sending --> CLOSE_WAIT");
                    client.status = Client.STATUS.CLOSE_WAIT;
                }
                else
                {
                    client.out.stop();
                    client.in.stop();

                    this.networkManager.closeTcpSocket(client.socketId);
                }
            }
        }
    }

    public void close(String ip)
    {

    }

    public void sendUDP(String ip, int port, ClavarChatMessage data)
    {
        this.networkManager.udpSend(data, ip, port);
    }

    public void sendTCP(String ip, int port, ClavarChatMessage data)
    {
        Log.Print(this.getClass().getName() + " Trying to send data to : " + ip + ":" + port);

        if (!this.clients.containsKey(ip))
        {
            Log.Print(this.getClass().getName() + " No client with ip : " + ip);
            Log.Print(this.getClass().getName() + " Creating client : " + ip);

            int socketId = this.networkManager.createSocket();
            int threadId = this.threadManager.createThread();

            Client client = new Client(socketId);
            client.status = Client.STATUS.CONNECTING;

            this.clients.put(ip, client);
            this.threadManager.setThreadRunnable(threadId, new TcpConnection(this.networkManager, socketId, ip, port));
            this.threadManager.startThread(threadId);
        }

        Log.Print(this.getClass().getName() + " Getting client : " + ip);
        Client client = this.clients.get(ip);

        if (client.status == Client.STATUS.CONNECTING)
        {
            Log.Print(this.getClass().getName() + " client : " + ip + " not connected, adding data to pending buffer");
            client.pendingDatasBuffer.push(data);
        }
        else
        {
            Log.Print(this.getClass().getName() + " sending data to " + ip + ":" + port);
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
                this.onConnectionSuccess((ConnectionEvent)event);
                break;
            case ConnectionEvent.CONNECTION_NEW:
                this.onConnectionNew((ConnectionEvent)event);
                break;
            case ConnectionEvent.CONNECTION_FAILED:
                this.connectionFailed((ConnectionEvent)event);
                break;
            case SocketDataEvent.SOCKET_DATA:
                this.onNetworkSocketData((SocketDataEvent)event);
                break;
            case SocketSendingEndEvent.FINISHED_SENDING:
                this.onFinisedSending((SocketSendingEndEvent)event);
                break;
        }
    }

    private void onFinisedSending(SocketSendingEndEvent event)
    {
        Log.Print(this.getClass().getName() + " [" + event.dstIp + "] Socket id : " + event.socketId + " finished sending");

        Client client = this.clients.get(event.dstIp);
        client.isSending = false;

        if (client.status == Client.STATUS.CLOSE_WAIT)
        {
            Log.Print(this.getClass().getName() + " Closing client : " + event.dstIp);

            client.in.stop();
            client.out.stop();

            this.clients.remove(event.dstIp);
            this.networkManager.closeTcpSocket(event.socketId);
        }
    }

    private void onNetworkSocketData(SocketDataEvent event)
    {
        ArrayList<String> ips = this.networkManager.getUserIp();

        if (!ips.contains(event.srcIp))
        {
            this.eventManager.notiy(new NetworkPacketEvent(event.srcIp, event.srcPort, event.data));
        }
        else
        {
            Log.Print(this.getClass().getName() + " Dropping paquet from " + event.srcIp + ":" + event.srcPort);
        }
    }

    private void onConnectionNew(ConnectionEvent event)
    {
        int dstPort = event.dstPort;
        int srcPort = event.srcPort;

        String dstIp = event.dstIp;
        String srcIp = event.srcIp;

        Client client = new Client(event.socketID);

        int threadInId  = this.threadManager.createThread();
        int threadOutId = this.threadManager.createThread();

        TCPIN in  = new TCPIN(this.networkManager, client.socketId);
        TCPOUT out = new TCPOUT(this.networkManager, client.socketId);

        client.srcIp = dstIp;
        client.dstIp = srcIp;

        client.srcPort = dstPort;
        client.dstPort = srcPort;

        client.in  = in;
        client.out = out;

        client.status = Client.STATUS.CONNECTED;

        this.threadManager.setThreadRunnable(threadInId, in);
        this.threadManager.setThreadRunnable(threadOutId, out);

        this.threadManager.startThread(threadInId);
        this.threadManager.startThread(threadOutId);

        this.clients.put(srcIp, client);

        Log.Print(this.getClass().getName() + " TCPIN  : " + dstIp + ":" + dstPort + " <-- " + srcIp + ":" + srcPort);
        Log.Print(this.getClass().getName() + " TCPOUT : " + dstIp + ":" + dstPort + " --> " + srcIp + ":" + srcPort);
    }

    private void onConnectionSuccess(ConnectionEvent event)
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

        client.status = (client.status == Client.STATUS.CLOSE_WAIT)?Client.STATUS.CLOSE_WAIT:Client.STATUS.CONNECTED;
        client.isSending = !client.pendingDatasBuffer.isEmpty();

        this.threadManager.setThreadRunnable(threadInId, in);
        this.threadManager.setThreadRunnable(threadOutId, out);

        this.threadManager.startThread(threadInId);
        this.threadManager.startThread(threadOutId);

        Log.Print(this.getClass().getName() + " Socket state : " + client.status);
        Log.Print(this.getClass().getName() + " TCPIN  : " + srcIp + ":" + srcPort + " <-- " + dstIp + ":" + dstPort);
        Log.Print(this.getClass().getName() + " TCPOUT : " + srcIp + ":" + srcPort + " --> " + dstIp + ":" + dstPort);

        this.flushPendingData(client);
    }

    private void connectionFailed(ConnectionEvent event)
    {
        if (clients.containsKey(event.dstIp))
        {
            Log.Print(this.getClass().getName() + " Removing client : " + event.dstIp);

            Client client = this.clients.get(event.dstIp);

            if (client.status == Client.STATUS.CONNECTED)
            {
                client.in.stop();
                client.out.stop();
            }

            this.clients.remove(event.dstIp);
        }
    }

    private void flushPendingData(Client client)
    {
        if (client.pendingDatasBuffer.size() > 0)
        {
            Log.Print(this.getClass().getName() + " Flushing data to TCP Out : ");
            while (!client.pendingDatasBuffer.isEmpty()) client.out.put(client.pendingDatasBuffer.removeLast());
        }
    }

    private class Client
    {
        public enum STATUS { IDLE, CONNECTING, CONNECTED, CLOSE_WAIT }

        private STATUS status;

        public boolean isSending;
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
            this.status = STATUS.IDLE;

            this.isSending = false;
            this.socketId = socketId;

            this.in = null;
            this.out = null;

            this.pendingDatasBuffer = new LinkedList<>();
        }
    }
}