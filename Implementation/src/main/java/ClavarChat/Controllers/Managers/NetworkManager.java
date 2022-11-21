package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.ClientHandler.ClientHandler;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Threads.*;
import ClavarChat.Models.Events.*;
import ClavarChat.Models.Events.Enums.EVENT_TYPE;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

//DEBUG//
import ClavarChat.Models.Paquets.Enums.PAQUET_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.CLI.CLI;

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

        this.DEBUG();
    }

    private void DEBUG()
    {
        ModuleCLI moduleNetworkCLI = new ModuleCLI();

        moduleNetworkCLI.addCommand("networks", () -> {
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

        moduleNetworkCLI.addCommand("send", () -> {
            String protocol = moduleNetworkCLI.getUserInput("TCP/UDP : ").toLowerCase();
            String pseudo = moduleNetworkCLI.getUserInput("Pseudo : ");
            String ip = moduleNetworkCLI.getUserInput("IP : ");
            String id = moduleNetworkCLI.getUserInput("ID : ");

            UserData user = new UserData(pseudo, id);
            Paquet paquet = new Paquet(user, PAQUET_TYPE.PAQUET_LOGIN, ip);

            switch (protocol)
            {
                case "tcp":
                    this.sendTCP(paquet);
                    break;
                case "udp":
                    this.sendUDP(paquet);
                    break;
            }
        });

        moduleNetworkCLI.addCommand("close-socket", () -> {
            this.closeAllTcp();
        });

        moduleNetworkCLI.addCommand("get-socket", () -> {
            ArrayList<String[]> sockets = this.getActiveSockets();
            for (String[] infos : sockets) System.out.println(infos[0] + ":" + infos[1] + " --> " + infos[2] + ":" + infos[3]);
        });

        CLI.installModule("network", moduleNetworkCLI);
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
            this.clients.get(ip).send(paquet);
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
            case NETWORK_EVENT_DATA:
                this.onNetworkDataEvent((DataEvent)event);
                break;
            case NETWORK_EVENT_CONNECTION:
                this.onConnectionEvent((ConnectionEvent)event);
                break;
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