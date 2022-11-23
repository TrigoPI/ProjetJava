package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.ClientHandler.ClientHandler;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;
import ClavarChat.Controllers.Threads.*;
import ClavarChat.Models.Events.*;

import java.io.*;
import java.net.*;
import java.util.*;

//DEBUG//
import ClavarChat.Utils.CLI.CLI;

public class NetworkManager implements Listener
{
    private int TCPPort;
    private int UDPPort;

    private EventManager eventManager;
    private NetworkThreadManager networkThreadManager;

    private TCPServerThread tcpServerThread;
    private UDPServerThread udpServerThread;

    private HashMap<String, LinkedList<Serializable>> pendingDatas;
    private HashMap<String, ClientHandler> clients;

    public NetworkManager(int TCPPort, int UDPPort)
    {
        this.TCPPort = TCPPort;
        this.UDPPort = UDPPort;

        this.eventManager = EventManager.getInstance();
        this.networkThreadManager = new NetworkThreadManager();

        this.tcpServerThread = this.networkThreadManager.createTCPServerThread(TCPPort);
        this.udpServerThread = this.networkThreadManager.createUDPServerThread(UDPPort);

        this.pendingDatas = new HashMap<String, LinkedList<Serializable>>();
        this.clients = new HashMap<String, ClientHandler>();

        this.eventManager.addEvent(EVENT_TYPE.NETWORK_EVENT);
        this.eventManager.addListenner(this, EVENT_TYPE.NETWORK_EVENT);

        this.DEBUG();
    }

    private void DEBUG()
    {
        ModuleCLI moduleCLI = new ModuleCLI();

        moduleCLI.addCommand("networks", () -> {
            ArrayList<String> allIp = this.getAllIp();
            ArrayList<String> allNetworks = this.getConnectedNetworks();
            ArrayList<String> allBroadcasts = this.getBroadcastAddresses();

            for (int i = 0; i < allIp.size(); i++)
            {
                String ip = allIp.get(i);
                String network = allNetworks.get(i);
                String broadcast = allBroadcasts.get(i);

                System.out.println("ip : " + ip + " - network : " + network + " - broadcast : " + broadcast);
            }
        });

        moduleCLI.addCommand("send", () -> {
            String protocol = moduleCLI.getUserInput("TCP/UDP : ").toLowerCase();
            String ip = moduleCLI.getUserInput("IP : ");

            switch (protocol)
            {
                case "tcp":
                    this.sendTCP("", ip);
                    break;
                case "udp":
                    this.sendUDP("", ip);
                    break;
            }
        });

        moduleCLI.addCommand("close-socket", () -> {
            this.closeAllTcp();
        });

        moduleCLI.addCommand("get-socket", () -> {
            ArrayList<String[]> sockets = this.getActiveSockets();
            for (String[] infos : sockets) System.out.println(infos[0] + ":" + infos[1] + " --> " + infos[2] + ":" + infos[3]);
        });

        moduleCLI.addCommand("start-server", () -> {
            this.startTCPServer();
            this.startUDPServer();
        });

        moduleCLI.addCommand("start-tcp", () -> {
            this.startTCPServer();
        });

        moduleCLI.addCommand("start-udp", () -> {
            this.startUDPServer();
        });

        CLI.installModule("network", moduleCLI);
    }

    public ArrayList<String> getAllIp()
    {
        return NetworkUtils.getAllIp();
    }

    public ArrayList<String[]> getActiveSockets()
    {
        ArrayList<String[]> sockets = new ArrayList<String[]>();

        for (String key : this.clients.keySet())
        {
            String[] info = new String[4];
            ClientHandler client = this.clients.get(key);

            info[1] = Integer.toString(client.getLocalPort());
            info[3] = Integer.toString(client.getDistantPort());
            info[0] = client.getLocalIP();
            info[2] = client.getDistantIP();

            sockets.add(info);
        }

        return sockets;
    }

    public ArrayList<String> getConnectedNetworks()
    {
        ArrayList<String> networks = new ArrayList<String>();
        ArrayList<String> ips = NetworkUtils.getAllIp();

        for (String ip : ips)
        {
            String mask = NetworkUtils.getNetworkMask(ip);
            networks.add(NetworkUtils.getNetwork(ip, mask));
        }

        return networks;
    }

    public ArrayList<String> getBroadcastAddresses()
    {
        ArrayList<String> broadcast = new ArrayList<String>();
        ArrayList<String> ips = NetworkUtils.getAllIp();
        for (String ip : ips) broadcast.add(NetworkUtils.getBroadcastAddress(ip));
        return broadcast;
    }

    public void startTCPServer()
    {
        this.tcpServerThread.start();
    }

    public void startUDPServer()
    {
        this.udpServerThread.start();
    }

    public void sendUDP(Serializable data, String dst)
    {
        try
        {
            InetAddress addr = InetAddress.getByName(dst);
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(data);
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

    public void sendTCP(Serializable data, String dst)
    {
        if (!this.clients.containsKey(dst))
        {
            if (!this.pendingDatas.containsKey(dst)) this.connect(dst);
            this.pendingDatas.get(dst).push(data);
        }
        else
        {
            this.clients.get(dst).send(data);
        }
    }

    public void closeAllTcp()
    {
        for (String key : this.clients.keySet()) this.closeTCP(key);
    }

    public void closeTCP(String ip)
    {
        if (!this.clients.containsKey(ip))
        {
            Log.Warning(this.getClass().getName() + " no client with ip : " + ip);
        }
        else
        {
            Log.Info(this.getClass().getName() + " close TCP with : " + ip);
            this.clients.get(ip).stop();
            this.clients.remove(ip);
        }
    }

    @Override
    public void onEvent(Event event)
    {
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
            case NETWORK_EVENT_PAQUET:
                this.onNetworkPaquetEvent((PaquetEvent)event);
                break;
            case NETWORK_EVENT_CONNECTION:
                this.onConnectionEvent((ConnectionEvent)event);
                break;
        }
    }

    private void onNetworkPaquetEvent(PaquetEvent event)
    {
        Log.Info(this.getClass().getName() + " new Paquet from : " + event.src + ":" + event.port);
        this.eventManager.notiy(new NetworkMessageEvent(event.data, event.src));
    }

    private void onConnectionEvent(ConnectionEvent event)
    {
        switch (event.connectionEventType)
        {
            case CONNECTION_EVENT_SUCCESS:
                this.onSuccessConnectionEvent((SuccessConectionEvent)event);
                break;
            case CONNECTION_EVENT_END:
                this.onEndConnectionEvent((EndConnectionEvent)event);
                break;
        }
    }

    private void onEndConnectionEvent(EndConnectionEvent event)
    {
        if (this.clients.containsKey(event.ip)) this.closeTCP(event.ip);
    }

    private void onSuccessConnectionEvent(SuccessConectionEvent event)
    {
        if (event.connected)
        {
            this.socketConnectedSuccess(event.socket, event.ip, event.port);
        }
        else
        {
            this.socketConnectedFailed(event.ip, event.port);
        }
    }

    private void socketConnectedSuccess(Socket socket, String ip, int port)
    {
        Log.Print(this.getClass().getName() + " creating new IN/OUT socket with " +  ip + ":" + port);

        TCPINSocketThread in = this.networkThreadManager.createTCPINSocketThread(socket);
        TCPOUTSocketThread out = this.networkThreadManager.createTCPOUTSocketThread(socket);

        this.clients.put(ip, new ClientHandler(socket, in, out));
        if (this.pendingDatas.containsKey(ip)) this.flushPendingDatas(ip, out);

        in.start();
        out.start();
    }

    private void socketConnectedFailed(String ip, int port)
    {
        Log.Print(this.getClass().getName() + " Removing pending data to : " + ip + ":" + port);
        this.pendingDatas.remove(ip);
    }

    private void flushPendingDatas(String ip, TCPOUTSocketThread out)
    {
        Log.Print(this.getClass().getName() + " Flushing data to " + out.getIdString() + " --> " + out);
        LinkedList<Serializable> datas = this.pendingDatas.get(ip);
        while (!datas.isEmpty()) out.send(datas.removeLast());
        this.pendingDatas.remove(ip);
    }

    private void connect(String ip)
    {
        Log.Print(this.getClass().getName() + " trying to connect : " + ip + ":" + this.TCPPort);
        this.pendingDatas.put(ip, new LinkedList<Serializable>());
        ConnectionThread connection = this.networkThreadManager.createConnectionThread(ip, this.TCPPort);
        connection.start();
    }
}