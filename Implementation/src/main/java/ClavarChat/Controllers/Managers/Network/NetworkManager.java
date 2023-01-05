package ClavarChat.Controllers.Managers.Network;

import ClavarChat.Models.NetworkPaquet.NetworkPaquet;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;
import ClavarChat.Utils.PackedArray.PackedArray;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class NetworkManager
{
    private final PackedArray<Socket> sockets;
    private final PackedArray<ServerSocket> tcpServers;
    private final PackedArray<DatagramSocket> udpServers;

    public NetworkManager()
    {
        this.sockets = new PackedArray<>();
        this.tcpServers = new PackedArray<>();
        this.udpServers = new PackedArray<>();
    }

    public ArrayList<String[]> getActiveSockets()
    {
        ArrayList<String[]> sockets = new ArrayList<>();

        for (Socket socket : this.sockets.getDatas())
        {
            String[] info = new String[4];

            if (socket.isConnected())
            {
                info[1] = Integer.toString(NetworkUtils.getSocketLocalPort(socket));
                info[3] = Integer.toString(NetworkUtils.getSocketDistantPort(socket));
                info[0] = NetworkUtils.getSocketLocalIp(socket);
                info[2] = NetworkUtils.getSocketDistantIp(socket);

                sockets.add(info);
            }
        }

        return sockets;
    }

    public ArrayList<String> getUserIp()
    {
        return NetworkUtils.getAllIp();
    }

    public ArrayList<String> getConnectedNetworks()
    {
        ArrayList<String> networks = new ArrayList<>();
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
        ArrayList<String> broadcast = new ArrayList<>();
        ArrayList<String> ips = NetworkUtils.getAllIp();
        for (String ip : ips) broadcast.add(NetworkUtils.getBroadcastAddress(ip));
        return broadcast;
    }

    public NetworkPaquet tcpReceive(int socketId)
    {
        Socket socket = this.sockets.get(socketId);
        NetworkPaquet data = null;

        if (socket == null)
        {
            Log.Error(this.getClass().getName() + " ERROR socket is null");
            return null;
        }

        try
        {
            InputStream in = socket.getInputStream();
            ObjectInputStream iin = new ObjectInputStream(in);
            data = (NetworkPaquet)iin.readObject();

            Log.Print(this.getClass().getName() + " data from " + data.srcIp + ":" + data.srcPort + " <-- " + data.dstIp + ":" + data.dstPort);
        }
        catch (IOException | ClassNotFoundException e)
        {
            Log.Error(this.getClass().getName() + " ERROR in TCP receive");
            Log.Print(this.getClass().getName() + " Removing socket : " + socketId);
            this.sockets.remove(socketId);
        }

        return data;
    }

    public NetworkPaquet udpReceive(int serverId)
    {
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        NetworkPaquet data = null;
        DatagramSocket server = this.udpServers.get(serverId);

        if (server == null)
        {
            Log.Error(this.getClass().getName() + " ERROR TCP server is null ");
            return null;
        }

        if (!server.isBound())
        {
            Log.Error(this.getClass().getName() + " ERROR UDP server not bound");
            return null;
        }

        if (server.isClosed())
        {
            Log.Error(this.getClass().getName() + " ERROR UDP server closed");
            return null;
        }

        try
        {
            DatagramPacket datagramPacket = new DatagramPacket(buffer, bufferSize);
            server.receive(datagramPacket);

            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
            data = (NetworkPaquet)iStream.readObject();

            Log.Print(this.getClass().getName() + " Paquet from UDP " + data.srcIp + ":" + data.srcPort);
        }
        catch (IOException | ClassNotFoundException e)
        {
            Log.Error(this.getClass().getName() + " ERROR UDP server closed");
        }

        return data;
    }

    public String getLocalSocketIp(int socketId)
    {
        Socket socket = this.sockets.get(socketId);
        if (socket == null && !socket.isConnected()) return null;
        return NetworkUtils.getSocketLocalIp(socket);
    }

    public String getDistantSocketIp(int socketId)
    {
        Socket socket = this.sockets.get(socketId);
        if (socket == null && !socket.isConnected()) return null;
        return NetworkUtils.getSocketDistantIp(socket);
    }

    public int getLocalSocketPort(int socketId)
    {
        Socket socket = this.sockets.get(socketId);
        if (socket == null && !socket.isConnected()) return -1;
        return NetworkUtils.getSocketLocalPort(socket);
    }

    public int getDistantSocketPort(int socketId)
    {
        Socket socket = this.sockets.get(socketId);
        if (socket == null && !socket.isConnected()) return -1;
        return NetworkUtils.getSocketDistantPort(socket);
    }

    public int createTcpServer()
    {
        int id = -1;
        try { id = this.tcpServers.add(new ServerSocket()); }
        catch (IOException e) { e.printStackTrace(); }
        return id;
    }

    public int createUdpServer()
    {
        int id = -1;
        try { id = this.udpServers.add(new DatagramSocket(null)); }
        catch (IOException e) { e.printStackTrace(); }
        return id;
    }

    public int createSocket()
    {
        return this.sockets.add(new Socket());
    }

    public int udpSend(Serializable data, String dst, int port)
    {
        int code = 0;

        try
        {
            InetAddress addr = InetAddress.getByName(dst);

            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.connect(addr, port);

            String srcIp = NetworkUtils.inetAddressToString(datagramSocket.getLocalAddress());
            int srcPort = datagramSocket.getLocalPort();

            NetworkPaquet paquet = new NetworkPaquet(srcIp, srcPort, dst, port, data);

            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(paquet);
            oo.close();

            byte[] serializedMessage = bStream.toByteArray();
            DatagramPacket datagramPacket = new DatagramPacket(serializedMessage, serializedMessage.length, addr, port);

            datagramSocket.send(datagramPacket);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            code = -1;
        }

        return code;
    }

    public int tcpSend(int socketId, Serializable data)
    {
        int code = 0;
        Socket socket = this.sockets.get(socketId);

        if (socket == null)
        {
            Log.Error(this.getClass().getName() + " ERROR socket is null ");
            return -1;
        }

        try
        {
            String srcIp = NetworkUtils.getSocketLocalIp(socket);
            String dstIp = NetworkUtils.getSocketDistantIp(socket);

            int srcPort = NetworkUtils.getSocketLocalPort(socket);
            int dstPort = NetworkUtils.getSocketDistantPort(socket);

            NetworkPaquet paquet = new NetworkPaquet(srcIp, srcPort, dstIp, dstPort, data);

            Log.Print(this.getClass().getName() + " Send data : " + srcIp + ":" + srcPort + " --> " + dstIp + ":" + dstPort);

            OutputStream out = socket.getOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(paquet);
        }
        catch (IOException e)
        {
            Log.Error(this.getClass().getName() + " ERROR in TCP Send");
            Log.Print(this.getClass().getName() + " Removing socket : " + socketId);

            this.sockets.remove(socketId);

            e.printStackTrace();

            code = -1;
        }

        return code;
    }

    public int connect(int socketId, String ip, int port)
    {
        int code = 0;
        Socket socket = this.sockets.get(socketId);

        if (socket == null)
        {
            Log.Error(this.getClass().getName() + " ERROR socket is null");
            return -1;
        }

        try
        {
            Log.Print(this.getClass().getName() + " Trying to connect with : " + ip + ":" + port + " --> socket id : " + socketId);

            InetAddress inetAddress = InetAddress.getByName(ip);
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
            socket.connect(socketAddress, 5000);

            Log.Info(this.getClass().getName() + " Connection success with : " + ip + ":" + port + " --> socket id : " + socketId);
        }
        catch (IOException e)
        {
            Log.Error(this.getClass().getName() + " Cannot connect to : " + ip + ":" + port);
            Log.Print(this.getClass().getName() + " Removing socket with id : " + socketId);

            this.sockets.remove(socketId);

            code = -1;
        }

        return code;
    }

    public int accept(int serverId)
    {
        int code = 0;
        ServerSocket server =  this.tcpServers.get(serverId);

        if (server == null)
        {
            Log.Error(this.getClass().getName() + " ERROR TCP server is null ");
            return -1;
        }

        if (!server.isBound())
        {
            Log.Error(this.getClass().getName() + " ERROR TCP server not bound");
            return -1;
        }

        if (server.isClosed())
        {
            Log.Error(this.getClass().getName() + " ERROR TCP server closed");
            return -1;
        }

        try
        {
            Socket socket = server.accept();
            int socketId = this.sockets.add(socket);

            String srcIp = NetworkUtils.getSocketLocalIp(socket);
            String dstIp = NetworkUtils.getSocketDistantIp(socket);

            int srcPort = NetworkUtils.getSocketLocalPort(socket);
            int dstPort = NetworkUtils.getSocketDistantPort(socket);

            Log.Info(this.getClass().getName() + " New TCP client : " + srcIp + ":" + srcPort + " <-- " + dstIp + ":" + dstPort);

            code = socketId;
        }
        catch (IOException e)
        {
            Log.Error(this.getClass().getName() + " ERROR in TCP server");
            code = -1;
        }

        return code;
    }

    public int startTcpServer(int serverId, int port)
    {
        int code = 0;
        ServerSocket server =  this.tcpServers.get(serverId);

        if (server == null)
        {
            Log.Error(this.getClass().getName() + " ERROR TCP server port : " + port + " is null ");
            return -1;
        }

        try
        {
            Log.Info(this.getClass().getName() + " Start TCP server on port : " + port);

            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            server.bind(inetSocketAddress);
        }
        catch (IOException e)
        {
            Log.Error(this.getClass().getName() + " ERROR in TCP TCP server");
            code = -1;
        }

        return code;
    }

    public int startUdpServer(int serverId, int port)
    {
        int code = 0;
        DatagramSocket server =  this.udpServers.get(serverId);

        if (server == null)
        {
            Log.Error(this.getClass().getName() + " ERROR TCP server is null ");
            return -1;
        }

        try
        {
            Log.Info(this.getClass().getName() + " Start UDP server on port : " + port);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            server.bind(inetSocketAddress);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            code = -1;
        }

        return code;
    }

    public void closeUdpServer(int serverID)
    {
        DatagramSocket server = this.udpServers.get(serverID);

        if (server == null)
        {
            Log.Error(this.getClass().getName() + " ERROR no UDP server with id : " + serverID);
            return;
        }

        int port = server.getLocalPort();
        server.close();
        Log.Error(this.getClass().getName() + " Closing UDP server with id : " + serverID + " --> " + port);
    }

    public void closeTcpServer(int serverID)
    {
        ServerSocket server = this.tcpServers.get(serverID);

        if (server == null)
        {
            Log.Error(this.getClass().getName() + " ERROR no TCP server with id : " + serverID);
            return;
        }

        try
        {
            int port = server.getLocalPort();
            server.close();
            Log.Error(this.getClass().getName() + " Closing TCP server with id : " + serverID + " --> " + port);
        }
        catch (IOException e)
        {
            Log.Error(this.getClass().getName() + " ERROR closeTcpServer");
        }
    }

    public void closeTcpSocket(int socketId)
    {
        Socket socket = this.sockets.get(socketId);

        if (socket == null)
        {
            Log.Error(this.getClass().getName() + " ERROR cannot close socket is null ");
            return;
        }

        if (!socket.isConnected())
        {
            Log.Warning(this.getClass().getName() + " Socket not connected ");
            this.sockets.remove(socketId);
            return;
        }

        try
        {
            String srcIp = NetworkUtils.getSocketLocalIp(socket);
            String dstIp = NetworkUtils.getSocketDistantIp(socket);

            int srcPort = NetworkUtils.getSocketLocalPort(socket);
            int dstPort = NetworkUtils.getSocketDistantPort(socket);

            Log.Info(this.getClass().getName() + " Closing Socket : " + srcIp + ":" + srcPort + " --> " + dstIp + ":" + dstPort);
            socket.close();

            Log.Info(this.getClass().getName() + " removing socket with id : " + socketId);
            this.sockets.remove(socketId);
        }
        catch (IOException e)
        {
            Log.Error(this.getClass().getName() + " ERROR closeTcpSocket ");
        }
    }
}