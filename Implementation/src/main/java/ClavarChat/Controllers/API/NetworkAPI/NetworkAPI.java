package ClavarChat.Controllers.API.NetworkAPI;

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
import ClavarChat.Models.ClvcMessage.*;
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

    private final HashMap<String, Messenger> messengers;
    private final ArrayList<MessageListener> listeners;

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
        for (String key : this.messengers.keySet())
        {
            Log.Info(this.getClass().getName() + " Closing " + key);
            Messenger messenger = this.messengers.get(key);
            messenger.socket.close();
        }

        this.messengers.clear();
    }

    public void addListener(MessageListener listener)
    {
        if (this.listeners.contains(listener))
        {
            Log.Warning(this.getClass().getName() + " Listener already registered");
            return;
        }

        this.listeners.add(listener);
    }

    @Override
    public void onNewConnection(int socketId, String srcIp, int srcPort, String dstIp, int dstPort)
    {
        ClvcSocket socket = new ClvcSocket(socketId, srcIp, srcPort, dstIp, dstPort, this.networkManager);
        TCPIN in  = new TCPIN(socket, this);
        TCPOUT out = new TCPOUT(socket);
        Messenger messenger = new Messenger(socket, in, out);

        int threadInId  = this.threadManager.createThread();
        int threadOutId = this.threadManager.createThread();

        this.threadManager.setRunnable(threadInId, messenger.in);
        this.threadManager.setRunnable(threadOutId, messenger.out);

        this.threadManager.startThread(threadInId);
        this.threadManager.startThread(threadOutId);

        this.messengers.put(dstIp, messenger);

        Log.Print(this.getClass().getName() + " TCPIN  : " + srcIp + ":" + srcPort + " <-- " + dstIp + ":" + dstPort);
        Log.Print(this.getClass().getName() + " TCPOUT : " + srcIp + ":" + srcPort + " --> " + dstIp + ":" + dstPort);
    }

    @Override
    public void onConnectionSuccess(String dstIp)
    {
        Messenger messenger = this.messengers.get(dstIp);

        int threadInId  = this.threadManager.createThread();
        int threadOutId = this.threadManager.createThread();

        this.threadManager.setRunnable(threadInId, messenger.in);
        this.threadManager.setRunnable(threadOutId, messenger.out);

        this.threadManager.startThread(threadInId);
        this.threadManager.startThread(threadOutId);

        Log.Print(this.getClass().getName() + " Socket state : " + messenger.socket.getState());
        Log.Print(this.getClass().getName() + " TCPIN  : " + messenger.socket.getSrcIp() + ":" + messenger.socket.getSrcPort() + " <-- " + messenger.socket.getDstIp() + ":" + messenger.socket.getDstPort());
        Log.Print(this.getClass().getName() + " TCPOUT : " + messenger.socket.getSrcIp() + ":" + messenger.socket.getSrcPort() + " --> " + messenger.socket.getDstIp() + ":" + messenger.socket.getDstPort());
    }

    @Override
    public void onConnectionFailed(int socketId, String dstIp)
    {
        if (messengers.containsKey(dstIp))
        {
            Log.Print(this.getClass().getName() + " Removing client : " + dstIp);
            Messenger messenger = this.messengers.get(dstIp);
            messenger.socket.close();
            this.messengers.remove(dstIp);
        }
    }

    @Override
    public void onPacket(String srcIp, int srcPort, String dstIp, int dstPort, ClvcMessage data)
    {
        ArrayList<String> ips = this.networkManager.getUserIp();

        if (ips.contains(srcIp))
        {
            Log.Print(this.getClass().getName() + " Dropping packet from " + srcIp + ":" + srcPort);
            return;
        }

        for (MessageListener listener : this.listeners)
        {
            listener.onData(srcIp, data);
        }
    }

    private void sendUDP(String ip, int port, ClvcMessage data)
    {
        this.networkManager.udpSend(data, ip, port);
    }

    private void sendTCP(String ip, int port, ClvcMessage data)
    {
        Log.Print(this.getClass().getName() + " Trying to send data to : " + ip + ":" + port);

        if (!this.messengers.containsKey(ip))
        {
            Log.Print(this.getClass().getName() + " No client with ip : " + ip);
            Log.Print(this.getClass().getName() + " Creating client : " + ip);

            int socketId = this.networkManager.createSocket();
            int threadId = this.threadManager.createThread();


            ClvcSocket socket = new ClvcSocket(socketId, ip, port, this.networkManager);
            TCPIN  in  = new TCPIN(socket, this);
            TCPOUT out = new TCPOUT(socket);
            Messenger messenger = new Messenger(socket, in, out);

            this.messengers.put(ip, messenger);

            this.threadManager.setRunnable(threadId, new TcpConnection(socket, this));
            this.threadManager.startThread(threadId);
        }

        Log.Print(this.getClass().getName() + " Getting client : " + ip);
        Messenger messenger = this.messengers.get(ip);
        messenger.socket.put(data);
    }

    private static class Messenger
    {
        public ClvcSocket socket;
        public TCPIN  in;
        public TCPOUT out;

        public Messenger(ClvcSocket socket, TCPIN in, TCPOUT out)
        {
            this.socket = socket;
            this.in  = in;
            this.out = out;
        }
    }
}