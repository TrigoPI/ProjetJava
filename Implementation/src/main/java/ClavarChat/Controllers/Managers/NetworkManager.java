package ClavarChat.Controllers.Managers;

import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Models.Events.Event.EVENT_TYPE;
import ClavarChat.Models.Events.*;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;
import ClavarChat.Utils.PackedArray.PackedArray;

import java.io.*;
import java.net.*;
import java.util.*;

//DEBUG//


public class NetworkManager
{
    private EventManager eventManager;

    private PackedArray<Socket> sockets;
    private PackedArray<ServerSocket> tcpServers;
    private PackedArray<DatagramSocket> udpServers;

    private HashMap<String, LinkedList<Serializable>> pendingDatas;

    public NetworkManager()
    {
        this.eventManager = EventManager.getInstance();

        this.sockets = new PackedArray<>();
        this.tcpServers = new PackedArray<>();
        this.udpServers = new PackedArray<>();

        this.pendingDatas = new HashMap<String, LinkedList<Serializable>>();

        this.eventManager.addEvent(EVENT_TYPE.EVENT_NETWORK_CONNECTION);
        this.eventManager.addEvent(EVENT_TYPE.EVENT_NETWORK_SOCKET_DATA);
    }

    public int createTcpServer()
    {
        int id = -1;
        try { id = this.tcpServers.add(new ServerSocket()); }
        catch (IOException e) {e.printStackTrace();}
        return id;
    }

    public int createUdpServer()
    {
        int id = -1;
        try { id = this.udpServers.add(new DatagramSocket()); }
        catch (IOException e) {e.printStackTrace();}
        return id;
    }

    public int createSocket()
    {
        return this.sockets.add(new Socket());
    }

    public boolean connect(int socketId, String ip, int port) throws IOException
    {
        Socket socket = this.sockets.get(socketId);

        if (socket != null)
        {
            Log.Print(this.getClass().getName() + " Trying to connect with : " + ip + ":" + port);

            InetAddress inetAddress = InetAddress.getByName(ip);
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
            socket.connect(socketAddress);

            return true;
        }
        else
        {
            Log.Error(this.getClass().getName() + " Cannot connect to : " + ip + ":" + port);
        }

        return false;
    }

    public void startTcpServer(int serverId, int port) throws IOException
    {
        ServerSocket server =  this.tcpServers.get(serverId);

        if (server != null)
        {
            Log.Info(this.getClass().getName() + " Start TCP server on port : " + port);

            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            server.bind(inetSocketAddress);

            while (!server.isClosed())
            {
                Socket socket = server.accept();

                int socketId = this.createSocket();
                int dstPort = socket.getPort();
                String dstIp = NetworkUtils.inetAddressToString(socket.getInetAddress());

                Log.Info(this.getClass().getName() + " New client : " + dstIp + ":" + port);

                this.eventManager.notiy(new ConnectionEvent(ConnectionEvent.CONNECTION_STATUS.SUCCESS, dstIp, dstPort, socketId));
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " TCP server port : " + port + "is null ");
        }
    }

    public void startUdpServer(int serverId, int port) throws SocketException
    {
        DatagramSocket server =  this.udpServers.get(serverId);

        if (server != null)
        {
            Log.Info(this.getClass().getName() + " Start UDP server on port : " + port);

            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            server.bind(inetSocketAddress);

//            while (!server.isClosed()) server.accept();
        }
        else
        {
            Log.Error(this.getClass().getName() + " TCP server port : " + port + "is null ");
        }
    }

    public void closeTcpServer(int serverID) throws IOException
    {
        ServerSocket server = this.tcpServers.get(serverID);

        if (server != null)
        {
            int port = server.getLocalPort();
            server.close();
            Log.Error(this.getClass().getName() + " No TCP server with id : " + serverID + " --> " + port);
        }
        else
        {
            Log.Error(this.getClass().getName() + " No TCP server with id : " + serverID);
        }
    }
}