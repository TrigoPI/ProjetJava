package ClavarChat.Controllers.API.NetworkAPI;

import ClavarChat.Controllers.Managers.Event.Listener;
import ClavarChat.Controllers.Managers.Event.EventManager;
import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Controllers.ThreadExecutable.Network.Connection.TcpConnection;
import ClavarChat.Controllers.ThreadExecutable.Network.Messagin.TCPIN;
import ClavarChat.Controllers.ThreadExecutable.Network.Messagin.TCPOUT;
import ClavarChat.Controllers.ThreadExecutable.Network.Server.TcpServer;
import ClavarChat.Controllers.ThreadExecutable.Network.Server.UdpServer;
import ClavarChat.Models.BytesImage.BytesImage;
import ClavarChat.Models.ClavarChatMessage.*;
import ClavarChat.Models.Events.Network.ConnectionEvent;
import ClavarChat.Models.Events.Event;
import ClavarChat.Models.Events.Network.NetworkPacketEvent;
import ClavarChat.Models.Events.Network.SocketDataEvent;
import ClavarChat.Models.Events.Network.SocketSendingEndEvent;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class NetworkAPI implements Listener
{
    public enum STATUS { IDLE, CONNECTING, CONNECTED, CLOSE_WAIT, CLOSED }

    private final int tcpPort;
    private final int udpPort;

    private final EventManager eventManager;
    private final ThreadManager threadManager;
    private final UserManager userManager;
    private final NetworkManager networkManager;

    private final HashMap<String, Client> clients;

    private final int tcpServerID;
    private final int udpServerID;

    public NetworkAPI(ThreadManager threadManager, UserManager userManager, int tcpPort, int udpPort)
    {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;

        this.networkManager = new NetworkManager();

        this.eventManager = EventManager.getInstance();
        this.threadManager = threadManager;
        this.userManager = userManager;

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

    public void sendLogin()
    {
        User user = this.userManager.getUser();
        byte[] avatar = this.userManager.getAvatar();
        ArrayList<User> users = this.userManager.getUsers();

        for (User other : users)
        {
            ArrayList<String> dst = this.userManager.getUserIP(other.id);
            this.sendTCP(dst.get(0), this.tcpPort, new LoginMessage(LoginMessage.LOGIN, user.pseudo, user.id, avatar));
        }

        this.closeAllClients();
    }

    public void sendLogout()
    {
        User user = this.userManager.getUser();

        if (this.userManager.isLogged())
        {
            Log.Error(this.getClass().getName() + " Cannot Logout, user not logged");
            return;
        }

        for (User other : this.userManager.getUsers())
        {
            LoginMessage message = new LoginMessage(LoginMessage.LOGOUT, user.pseudo, user.id);
            this.sendTCP(this.userManager.getUserIP(other.id).get(0), this.tcpPort, message);
        }
    }

    public void sendMessage(int userId, String text)
    {
        if (this.userManager.isLogged())
        {
            Log.Error(this.getClass().getName() + " Cannot send message, user not logged");
            return;
        }

        User user = this.userManager.getUser();
        String ip = this.userManager.getUserIP(userId).get(0);

        TextMessage mgs = new TextMessage(user.pseudo, user.id, text);
        this.sendTCP(ip, this.tcpPort, mgs);
    }

    public void sendDiscoverResponse(String src)
    {
        if (this.userManager.isLogged())
        {
            Log.Error(this.getClass().getName() + " User not logged cannot respond to DISCOVER");
            return;
        }

        int count = this.userManager.getUserCount();
        byte[] avatar = this.userManager.getAvatar();
        User user = this.userManager.getUser();

        DiscoverResponseMessage informationMessage = new DiscoverResponseMessage(user.pseudo, user.id, avatar, count);
        this.sendTCP(src, this.tcpPort, informationMessage);
    }

    public void sendDiscoverRequest()
    {
        ArrayList<String> broadcast = this.getBroadcastAddresses();
        for (String address : broadcast) this.sendUDP(address, this.udpPort, new DiscoverRequestMessage());
    }

    public void startServer()
    {
        int tcpThreadID = this.threadManager.createThread(new TcpServer(this.networkManager, this.tcpServerID, this.tcpPort));
        int udpThreadID = this.threadManager.createThread(new UdpServer(this.networkManager, this.udpServerID, this.udpPort));

        this.threadManager.startThread(tcpThreadID);
        this.threadManager.startThread(udpThreadID);
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

            if (client.status == STATUS.CONNECTING)
            {
                Log.Info(this.getClass().getName() + " Cannot close : " + key + " because socket is connecting --> CLOSE_WAIT");
                client.status = STATUS.CLOSE_WAIT;
            }

            if (client.status == STATUS.CONNECTED)
            {
                if (client.isSending)
                {
                    Log.Info(this.getClass().getName() + " Cannot close : " + key + " because socket is sending --> CLOSE_WAIT");
                    client.status = STATUS.CLOSE_WAIT;
                }
                else
                {
                    client.status = STATUS.CLOSED;
                    client.out.stop();
                    client.in.stop();

                    this.networkManager.closeTcpSocket(client.socketId);
                }
            }
        }
    }

    @Override
    public void onEvent(Event event)
    {
        switch (event.type) {
            case ConnectionEvent.CONNECTION_SUCCESS -> this.onConnectionSuccess((ConnectionEvent) event);
            case ConnectionEvent.CONNECTION_NEW -> this.onConnectionNew((ConnectionEvent) event);
            case ConnectionEvent.CONNECTION_FAILED -> this.connectionFailed((ConnectionEvent) event);
            case SocketDataEvent.SOCKET_DATA -> this.onNetworkSocketData((SocketDataEvent) event);
            case SocketSendingEndEvent.FINISHED_SENDING -> this.onFinisedSending((SocketSendingEndEvent) event);
        }
    }

    private void onFinisedSending(SocketSendingEndEvent event)
    {
        Client client = this.clients.get(event.dstIp);
        client.isSending = false;

        Log.Print(this.getClass().getName() + " [" + event.dstIp + "] Socket id : " + event.socketId + " finished sending");
        Log.Print(this.getClass().getName() + " [" + event.dstIp + "] Socket id : " + event.socketId + " Status : " + client.status);

        if (client.status == STATUS.CLOSE_WAIT)
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

        client.status = STATUS.CONNECTED;

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

        client.status = (client.status == STATUS.CLOSE_WAIT)?STATUS.CLOSE_WAIT:STATUS.CONNECTED;
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

            if (client.status == STATUS.CONNECTED)
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

    private void sendUDP(String ip, int port, ClavarChatMessage data)
    {
        this.networkManager.udpSend(data, ip, port);
    }

    private void sendTCP(String ip, int port, ClavarChatMessage data)
    {
        Log.Print(this.getClass().getName() + " Trying to send data to : " + ip + ":" + port);

        if (!this.clients.containsKey(ip))
        {
            Log.Print(this.getClass().getName() + " No client with ip : " + ip);
            Log.Print(this.getClass().getName() + " Creating client : " + ip);

            int socketId = this.networkManager.createSocket();
            int threadId = this.threadManager.createThread();

            Client client = new Client(socketId);
            client.status = STATUS.CONNECTING;

            this.clients.put(ip, client);
            this.threadManager.setThreadRunnable(threadId, new TcpConnection(this.networkManager, socketId, ip, port));
            this.threadManager.startThread(threadId);
        }

        Log.Print(this.getClass().getName() + " Getting client : " + ip);
        Client client = this.clients.get(ip);

        if (client.status != STATUS.CLOSED && client.status != STATUS.CLOSE_WAIT)
        {
            if (client.status == STATUS.CONNECTING)
            {
                Log.Print(this.getClass().getName() + " client : " + ip + " not connected, adding data to pending buffer");
                client.pendingDatasBuffer.push(data);
            }
            else
            {
                Log.Print(this.getClass().getName() + " sending data to " + ip + ":" + port);
                client.isSending = true;
                client.out.put(data);
            }
        }
        else
        {
            Log.Info(this.getClass().getName() + " Cannot send data because client is : " + client.status);
        }
    }

    private static class Client
    {
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