package ClavarChat.Controllers.API.NetworkAPI;

import ClavarChat.Controllers.ClavarChatRunnable.Network.TCPIN.TCPIN;
import ClavarChat.Controllers.ClavarChatRunnable.Network.TCPOUT.TCPOUT;
import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Controllers.ClavarChatRunnable.Network.Connection.TcpConnection;
import ClavarChat.Controllers.ClavarChatRunnable.Network.Server.TcpServer;
import ClavarChat.Controllers.ClavarChatRunnable.Network.Server.UdpServer;
import ClavarChat.Models.ClavarChatListener.NetworkListener;
import ClavarChat.Models.ClavarChatMessage.*;
import ClavarChat.Models.ClavarChatSocket.ClavarChatSocket;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class NetworkAPI implements NetworkListener
{
    public enum STATUS { IDLE, CONNECTING, CONNECTED, CLOSE_WAIT, CLOSED }

    private final int tcpPort;
    private final int udpPort;

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

        this.threadManager = threadManager;
        this.userManager = userManager;

        this.tcpServerID = this.networkManager.createTcpServer();
        this.udpServerID = this.networkManager.createUdpServer();

        this.clients = new HashMap<>();
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
        int tcpThreadID = this.threadManager.createThread(new TcpServer(this.networkManager, this, this.tcpServerID, this.tcpPort));
        int udpThreadID = this.threadManager.createThread(new UdpServer(this.networkManager, this, this.udpServerID, this.udpPort));

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
    public void onFinishedSending(int socketId, String dstIp)
    {
        Client client = this.clients.get(dstIp);
        client.isSending = false;

        Log.Print(this.getClass().getName() + " [" + dstIp + "] Socket id : " + socketId + " finished sending");
        Log.Print(this.getClass().getName() + " [" + dstIp + "] Socket id : " + socketId + " Status : " + client.status);

        if (client.status == STATUS.CLOSE_WAIT)
        {
            Log.Print(this.getClass().getName() + " Closing client : " + dstIp);

            client.in.stop();
            client.out.stop();

            this.clients.remove(dstIp);
            this.networkManager.closeTcpSocket(socketId);
        }
    }

    @Override
    public void onNewConnection(int socketId, String srcIp, int srcPort, String dstIp, int dstPort)
    {
        Client client = new Client(socketId);
        ClavarChatSocket socket = new ClavarChatSocket(socketId, srcIp, srcPort, dstIp, dstPort, this.networkManager);

        int threadInId  = this.threadManager.createThread();
        int threadOutId = this.threadManager.createThread();

        TCPIN in  = new TCPIN(socket, this);
        TCPOUT out = new TCPOUT(socket, this);

        client.in  = in;
        client.out = out;

        client.status = STATUS.CONNECTED;

        this.threadManager.setRunnable(threadInId, in);
        this.threadManager.setRunnable(threadOutId, out);

        this.threadManager.startThread(threadInId);
        this.threadManager.startThread(threadOutId);

        this.clients.put(srcIp, client);

        Log.Print(this.getClass().getName() + " TCPIN  : " + dstIp + ":" + dstPort + " <-- " + srcIp + ":" + srcPort);
        Log.Print(this.getClass().getName() + " TCPOUT : " + dstIp + ":" + dstPort + " --> " + srcIp + ":" + srcPort);
    }

    @Override
    public void onConnectionSuccess(String dstIp)
    {
        Client client = this.clients.get(dstIp);
        ClavarChatSocket socket = client.socket;

        int threadInId  = this.threadManager.createThread();
        int threadOutId = this.threadManager.createThread();

        TCPIN in  = new TCPIN(socket, this);
        TCPOUT out = new TCPOUT(socket, this);

        client.in  = in;
        client.out = out;

        client.status = (client.status == STATUS.CLOSE_WAIT)?STATUS.CLOSE_WAIT:STATUS.CONNECTED;
        client.isSending = !client.pendingDataBuffer.isEmpty();

        this.threadManager.setRunnable(threadInId, in);
        this.threadManager.setRunnable(threadOutId, out);

        this.threadManager.startThread(threadInId);
        this.threadManager.startThread(threadOutId);

        Log.Print(this.getClass().getName() + " Socket state : " + client.status);
        Log.Print(this.getClass().getName() + " TCPIN  : " + socket.getSrcIp() + ":" + socket.getSrcPort() + " <-- " + socket.getDstIp() + ":" + socket.getDstPort());
        Log.Print(this.getClass().getName() + " TCPOUT : " + socket.getSrcIp() + ":" + socket.getSrcPort() + " --> " + socket.getDstIp() + ":" + socket.getDstPort());

        this.flushPendingData(client);
    }

    @Override
    public void onConnectionFailed(int socketId, String dstIp)
    {
        if (clients.containsKey(dstIp))
        {
            Log.Print(this.getClass().getName() + " Removing client : " + dstIp);

            Client client = this.clients.get(dstIp);

            if (client.status == STATUS.CONNECTED)
            {
                client.in.stop();
                client.out.stop();
            }

            this.clients.remove(dstIp);
        }
    }

    @Override
    public void onData(String srcIp, int srcPort, String dstIp, int dstPort, ClavarChatMessage data)
    {
        ArrayList<String> ips = this.networkManager.getUserIp();

        if (!ips.contains(srcIp))
        {
//            this.eventManager.notify(new NetworkPacketEvent(event.srcIp, event.srcPort, event.data));
        }
        else
        {
            Log.Print(this.getClass().getName() + " Dropping packet from " + srcIp + ":" + srcPort);
        }
    }

    private void flushPendingData(Client client)
    {
        if (client.pendingDataBuffer.size() > 0)
        {
            Log.Print(this.getClass().getName() + " Flushing data to TCP Out : ");
            while (!client.pendingDataBuffer.isEmpty()) client.out.put(client.pendingDataBuffer.removeLast());
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

            ClavarChatSocket socket = new ClavarChatSocket(socketId, ip, port, this.networkManager);

            this.clients.put(ip, client);
            this.threadManager.setRunnable(threadId, new TcpConnection(socket, this.networkManager, this));
            this.threadManager.startThread(threadId);
        }

        Log.Print(this.getClass().getName() + " Getting client : " + ip);
        Client client = this.clients.get(ip);

        if (client.status != STATUS.CLOSED && client.status != STATUS.CLOSE_WAIT)
        {
            if (client.status == STATUS.CONNECTING)
            {
                Log.Print(this.getClass().getName() + " client : " + ip + " not connected, adding data to pending buffer");
                client.pendingDataBuffer.push(data);
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
        public STATUS status;
        public ClavarChatSocket socket;
        public TCPIN in;
        public TCPOUT out;
        public int socketId;
        public boolean isSending;

        public LinkedList<Serializable> pendingDataBuffer;

        public Client(int socketId)
        {
            this.status = STATUS.IDLE;

            this.isSending = false;
            this.socketId = socketId;

            this.socket = null;
            this.in = null;
            this.out = null;

            this.pendingDataBuffer = new LinkedList<>();
        }
    }
}