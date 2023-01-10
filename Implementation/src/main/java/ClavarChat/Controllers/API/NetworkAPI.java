package ClavarChat.Controllers.API;

import ClavarChat.Controllers.Runnables.Network.SocketObserver.SocketObserver;
import ClavarChat.Controllers.Runnables.Network.TCPIN.TCPIN;
import ClavarChat.Controllers.Runnables.Network.TCPOUT.TCPOUT;
import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Controllers.Managers.Thread.ThreadManager;
import ClavarChat.Controllers.Managers.User.UserManager;
import ClavarChat.Controllers.Runnables.Network.Connection.TcpConnection;
import ClavarChat.Controllers.Runnables.Network.Server.TcpServer;
import ClavarChat.Controllers.Runnables.Network.Server.UdpServer;
import ClavarChat.Models.ClvcListener.MessageListener;
import ClavarChat.Models.ClvcListener.NetworkListener;
import ClavarChat.Models.ClvcNetworkMessage.*;
import ClavarChat.Models.ClvcMessenger.ClvcMessenger;
import ClavarChat.Models.ClvcSocket.ClvcSocket;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class NetworkAPI implements NetworkListener
{
    public enum STATUS { IDLE, CONNECTING, CONNECTED, CLOSE_WAIT, CLOSED }

    private final int tcpPort;
    private final int udpPort;

    private final ThreadManager threadManager;
    private final UserManager userManager;
    private final NetworkManager networkManager;

    private final HashMap<String, ClvcMessenger> messengers;
    private final ArrayList<MessageListener> listeners;

    private final SocketObserver socketObserver;

    private final int tcpServerID;
    private final int udpServerID;

    public NetworkAPI(UserManager userManager, int tcpPort, int udpPort)
    {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;

        this.networkManager = new NetworkManager();
        this.threadManager = new ThreadManager();

        this.userManager = userManager;

        this.tcpServerID = this.networkManager.createTcpServer();
        this.udpServerID = this.networkManager.createUdpServer();

        this.messengers = new HashMap<>();
        this.listeners = new ArrayList<>();

        this.socketObserver = new SocketObserver();

        int threadId = this.threadManager.createThread(this.socketObserver);
        this.threadManager.startThread(threadId);
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

    public void sendMessage(int userId, String sharedId, String text)
    {
        if (this.userManager.isLogged())
        {
            Log.Error(this.getClass().getName() + " Cannot send message, user not logged");
            return;
        }

        if (!this.userManager.isConnected(userId))
        {
            Log.Warning(this.getClass().getName() + " User : " + userId + " not connected");
            return;
        }

        User user = this.userManager.getUser();
        String ip = this.userManager.getUserIP(userId).get(0);

        TextMessage mgs = new TextMessage(user.id, sharedId, user.pseudo, text);
        this.sendTCP(ip, this.tcpPort, mgs);
    }

    public void sendSharedConversationId(int userId, String sharedId)
    {
        int id = this.userManager.getId();
        String pseudo = this.userManager.getPseudo();
        String ip = this.userManager.getUserIP(userId).get(0);
        SharedIdMessage msg = new SharedIdMessage(sharedId, id, pseudo);
        this.sendTCP(ip, this.tcpPort, msg);
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

    public void closeSocketObserver()
    {
        this.socketObserver.stop();
    }

    public void closeServer()
    {
        this.networkManager.closeUdpServer(this.udpServerID);
        this.networkManager.closeTcpServer(this.tcpServerID);
    }

    public void closeClient(int userId)
    {
        String ip = this.userManager.getUserIP(userId).get(0);

        if (!this.messengers.containsKey(ip))
        {
            Log.Warning(this.getClass().getName() + " No client with ip : " + ip);
            return;
        }

        ClvcMessenger messenger = this.messengers.get(ip);
        Log.Info(this.getClass().getName() + " Closing " + ip);
        messenger.socket.close();
    }

    public void closeAllClients()
    {
        for (String key : this.messengers.keySet())
        {
            Log.Info(this.getClass().getName() + " Closing " + key);
            ClvcMessenger messenger = this.messengers.get(key);
            messenger.socket.close();
        }
    }

    public void addListener(MessageListener listener)
    {
        if (this.listeners.contains(listener))
        {
            Log.Warning(this.getClass().getName() + " Listener " + listener.getClass().getName() + " already registered");
            return;
        }

        this.listeners.add(listener);
    }

    @Override
    public void onMessengerFinished(String dstIp)
    {
        ClvcMessenger messenger = this.messengers.get(dstIp);

        if (!messenger.out.isFinished()) return;
        if (!messenger.in.isFinished()) return;
        Log.Print(this.getClass().getName() + " Removing client : " + dstIp + " connection ended");
        messenger.socket.forceClose();
        this.messengers.remove(dstIp);
    }

    @Override
    public void onNewConnection(int socketId, String srcIp, int srcPort, String dstIp, int dstPort)
    {
        Log.Print(this.getClass().getName() + " Creating client " + srcIp);

        ClvcSocket socket = new ClvcSocket(socketId, srcIp, srcPort, dstIp, dstPort, this.networkManager);
        TCPIN in  = new TCPIN(socket, this);
        TCPOUT out = new TCPOUT(socket, this);
        ClvcMessenger messenger = new ClvcMessenger(socket, in, out);

        this.runMessenger(messenger);

        this.messengers.put(srcIp, messenger);
        this.socketObserver.addMessenger(messenger);
    }

    @Override
    public void onConnectionSuccess(String dstIp)
    {
        ClvcMessenger messenger = this.messengers.get(dstIp);
        this.runMessenger(messenger);
        Log.Print(this.getClass().getName() + " Socket state : " + messenger.socket.getState());
    }

    @Override
    public void onConnectionFailed(int socketId, String dstIp)
    {
        if (!this.messengers.containsKey(dstIp)) return;
        Log.Print(this.getClass().getName() + " Removing client : " + dstIp + " connection failed");
        this.messengers.remove(dstIp);

    }

    @Override
    public void onPacket(String from, int tcpPort, ClvcNetworkMessage data)
    {
        ArrayList<String> ips = this.networkManager.getUserIp();

        if (ips.contains(from))
        {
            Log.Print(this.getClass().getName() + " Dropping packet from " + from + ":" + tcpPort);
            return;
        }

        if (this.messengers.containsKey(from))
        {
            this.messengers.get(from).resetTimer();
        }

        for (MessageListener listener : this.listeners)
        {
            listener.onData(from, data);
        }

    }

    private void sendUDP(String ip, int port, ClvcNetworkMessage data)
    {
        this.networkManager.udpSend(data, ip, port);
    }

    private void sendTCP(String ip, int port, ClvcNetworkMessage data)
    {
        Log.Print(this.getClass().getName() + " Trying to send data to : " + ip + ":" + port);
        this.createNewClient(ip, port);
        this.putDataToSocket(ip, data);
        this.sendDataToNetwork(ip);
    }

    private void sendDataToNetwork(String ip)
    {
        ClvcMessenger messenger = this.messengers.get(ip);
        if (!messenger.out.isFinished()) return;
        int threadId = this.threadManager.createThread(messenger.out);
        this.threadManager.startThread(threadId);
    }

    private void putDataToSocket(String ip, ClvcNetworkMessage data)
    {
        Log.Print(this.getClass().getName() + " Getting client : " + ip);
        ClvcMessenger messenger = this.messengers.get(ip);
        messenger.socket.put(data);
        messenger.resetTimer();
    }

    private void createNewClient(String ip, int port)
    {
        if (this.messengers.containsKey(ip)) return;

        Log.Print(this.getClass().getName() + " No client with ip : " + ip);
        Log.Print(this.getClass().getName() + " Creating client : " + ip);

        int socketId = this.networkManager.createSocket();
        int threadId = this.threadManager.createThread();

        ClvcSocket socket = new ClvcSocket(socketId, ip, port, this.networkManager);
        TCPIN  in  = new TCPIN(socket, this);
        TCPOUT out = new TCPOUT(socket, this);
        ClvcMessenger messenger = new ClvcMessenger(socket, in, out);

        this.socketObserver.addMessenger(messenger);
        this.messengers.put(ip, messenger);

        this.threadManager.setRunnable(threadId, new TcpConnection(socket, this));
        this.threadManager.startThread(threadId);

    }

    private void runMessenger(ClvcMessenger messenger)
    {
        int threadInId  = this.threadManager.createThread();
        int threadOutId = this.threadManager.createThread();

        this.threadManager.setRunnable(threadInId, messenger.in);
        this.threadManager.setRunnable(threadOutId, messenger.out);

        this.threadManager.startThread(threadInId);
        this.threadManager.startThread(threadOutId);
    }
}