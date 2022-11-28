package ClavarChat.Controllers.Managers;

import ClavarChat.Utils.NetworkUtils.NetworkUtils;
import ClavarChat.Utils.PackedArray.PackedArray;
import ClavarChat.Utils.Log.Log;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class NetworkManager
{
    private PackedArray<Socket> sockets;
    private PackedArray<ServerSocket> tcpServers;
    private PackedArray<DatagramSocket> udpServers;

    public NetworkManager()
    {
        this.sockets = new PackedArray<>();
        this.tcpServers = new PackedArray<>();
        this.udpServers = new PackedArray<>();
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
        ArrayList<String> broadcast = new ArrayList<>();
        ArrayList<String> ips = NetworkUtils.getAllIp();
        for (String ip : ips) broadcast.add(NetworkUtils.getBroadcastAddress(ip));
        return broadcast;
    }

    public Serializable tcpReceive(int socketId)
    {
        Serializable data = null;
        Socket socket = this.sockets.get(socketId);

        if (socket != null)
        {
            try
            {
                String srcIp = NetworkUtils.getSocketLocalIp(socket);
                String dstIp = NetworkUtils.getSocketDistantIp(socket);

                int srcPort = NetworkUtils.getSocketLocalPort(socket);
                int dstPort = NetworkUtils.getSocketDistantPort(socket);

                InputStream in = socket.getInputStream();
                ObjectInputStream iin = new ObjectInputStream(in);
                data = (Serializable)iin.readObject();

                Log.Print(this.getClass().getName() + " data from " + srcIp + ":" + srcPort + " <-- " + dstIp + ":" + dstPort);
            }
            catch (IOException | ClassNotFoundException e)
            {
                Log.Error(this.getClass().getName() + " ERROR in TCP receive");
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " ERROR socket is null ");
        }

        return data;
    }

    public Serializable udpReceive(int serverId)
    {
        DatagramSocket server =  this.udpServers.get(serverId);

        if (server != null)
        {
            if (server.isBound() && !server.isClosed())
            {
                try
                {
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];

                    DatagramPacket datagramPacket = new DatagramPacket(buffer, bufferSize);
                    server.receive(datagramPacket);

                    Log.Print(this.getClass().getName() + " Paquet from UDP");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.Error(this.getClass().getName() + " ERROR TCP server not bound");
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " ERROR TCP server is null ");
        }

        return null;
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
        catch (IOException e) {e.printStackTrace();}
        return id;
    }

    public int createUdpServer()
    {
        int id = -1;
        try { id = this.udpServers.add(new DatagramSocket(null)); }
        catch (IOException e) {e.printStackTrace();}
        return id;
    }

    public int createSocket()
    {
        return this.sockets.add(new Socket());
    }

    public int tcpSend(int socketId, Serializable data)
    {
        Socket socket = this.sockets.get(socketId);

        if (socket != null)
        {
            try
            {
                String srcIp = NetworkUtils.getSocketLocalIp(socket);
                String dstIp = NetworkUtils.getSocketDistantIp(socket);

                int srcPort = NetworkUtils.getSocketLocalPort(socket);
                int dstPort = NetworkUtils.getSocketDistantPort(socket);

                Log.Print(this.getClass().getName() + " Send data : " + srcIp + ":" + srcPort + " --> " + dstIp + ":" + dstPort);

                OutputStream out = socket.getOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(out);
                oout.writeObject(data);

                return 0;
            }
            catch (IOException e)
            {
                Log.Error(this.getClass().getName() + " ERROR in TCP Send");
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " ERROR socket is null ");
        }

        return -1;
    }

    public int connect(int socketId, String ip, int port)
    {
        Socket socket = this.sockets.get(socketId);

        if (socket != null)
        {
            try
            {
                Log.Print(this.getClass().getName() + " Trying to connect with : " + ip + ":" + port + " --> socket id : " + socketId);

                InetAddress inetAddress = InetAddress.getByName(ip);
                SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
                socket.connect(socketAddress, 5000);

                Log.Info(this.getClass().getName() + " Connection success with : " + ip + ":" + port + " --> socket id : " + socketId);

                return 0;
            }
            catch (IOException e)
            {
                Log.Error(this.getClass().getName() + " Cannot connect to : " + ip + ":" + port);
                Log.Print(this.getClass().getName() + " Removing socket with id : " + socketId);

                this.sockets.remove(socketId);
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " ERROR socket is null");
        }

        return -1;
    }

    public int accept(int serverId)
    {
        ServerSocket server =  this.tcpServers.get(serverId);

        if (server != null)
        {
            if (server.isBound() && !server.isClosed())
            {
                try
                {
                    Socket socket = server.accept();
                    int socketId = this.sockets.add(socket);

                    String srcIp = NetworkUtils.getSocketLocalIp(socket);
                    String dstIp = NetworkUtils.getSocketDistantIp(socket);

                    int srcPort = NetworkUtils.getSocketLocalPort(socket);
                    int dstPort = NetworkUtils.getSocketDistantPort(socket);

                    Log.Info(this.getClass().getName() + " New TCP client : " + srcIp + ":" + srcPort + " <-- " + dstIp + ":" + dstPort);
                    return socketId;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.Error(this.getClass().getName() + " ERROR TCP server not bound");
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " ERROR TCP server is null ");
        }

        return -1;
    }

    public int startTcpServer(int serverId, int port)
    {
        ServerSocket server =  this.tcpServers.get(serverId);

        if (server != null)
        {
            Log.Info(this.getClass().getName() + " Start TCP server on port : " + port);

            try
            {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
                server.bind(inetSocketAddress);
                return 0;
            }
            catch (IOException e)
            {
                Log.Error(this.getClass().getName() + " ERROR in TCP TCP server");
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " ERROR TCP server port : " + port + " is null ");
        }

        return -1;
    }

    public int startUdpServer(int serverId, int port)
    {
        DatagramSocket server =  this.udpServers.get(serverId);

        if (server != null)
        {
            Log.Info(this.getClass().getName() + " Start UDP server on port : " + port);

            try
            {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
                server.bind(inetSocketAddress);
            }
            catch (SocketException e)
            {
                e.printStackTrace();
            }
            return 0;
        }
        else
        {
            Log.Error(this.getClass().getName() + " ERROR TCP server is null ");
        }

        return -1;
    }

    public void closeTcpServer(int serverID)
    {
        ServerSocket server = this.tcpServers.get(serverID);

        if (server != null)
        {

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
        else
        {
            Log.Error(this.getClass().getName() + " ERROR no TCP server with id : " + serverID);
        }
    }

    public void closeTcpSocket(int socketId)
    {
        Socket socket = this.sockets.get(socketId);

        if (socket != null)
        {
            try
            {
                String srcIp = NetworkUtils.getSocketLocalIp(socket);
                String dstIp = NetworkUtils.getSocketDistantIp(socket);

                int srcPort = NetworkUtils.getSocketLocalPort(socket);
                int dstPort = NetworkUtils.getSocketDistantPort(socket);


                socket.close();
                Log.Error(this.getClass().getName() + " Closing Socket server with id : " + srcIp + ":" + srcPort + " --> " + dstIp + ":" + dstPort);
            }
            catch (IOException e)
            {
                Log.Error(this.getClass().getName() + " ERROR closeTcpSocket");
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " ERROR cannot close socket is null ");
        }
    }
}