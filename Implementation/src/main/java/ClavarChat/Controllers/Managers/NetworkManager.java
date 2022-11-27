package ClavarChat.Controllers.Managers;

import ClavarChat.Models.Events.Event.EVENT_TYPE;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;
import ClavarChat.Utils.PackedArray.PackedArray;
import ClavarChat.Models.Events.ConnectionEvent.CONNECTION_STATUS;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Models.Events.*;

import java.io.*;
import java.net.*;

public class NetworkManager
{
    private EventManager eventManager;

    private PackedArray<Socket> sockets;
    private PackedArray<ServerSocket> tcpServers;
    private PackedArray<DatagramSocket> udpServers;

    public NetworkManager()
    {
        this.eventManager = EventManager.getInstance();

        this.sockets = new PackedArray<>();
        this.tcpServers = new PackedArray<>();
        this.udpServers = new PackedArray<>();

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
        try { id = this.udpServers.add(new DatagramSocket(null)); }
        catch (IOException e) {e.printStackTrace();}
        return id;
    }

    public int createSocket()
    {
        return this.sockets.add(new Socket());
    }

    public void tcpReceive(int socketId)
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

                InputStream in = socket.getInputStream();
                ObjectInputStream iin = new ObjectInputStream(in);
                Serializable data = (Serializable)iin.readObject();

                Log.Print(this.getClass().getName() + " data from " + srcIp + ":" + srcPort + " <-- " + dstIp + ":" + dstPort);

                this.eventManager.notiy(new SocketDataEvent(dstIp, dstPort, data));
            }
            catch (IOException | ClassNotFoundException e)
            {
                Log.Error(this.getClass().getName() + " Error in TCP receive");

                String dstIp = NetworkUtils.getSocketDistantIp(socket);
                int dstPort = NetworkUtils.getSocketDistantPort(socket);

                this.eventManager.notiy(new ConnectionEvent(CONNECTION_STATUS.FAILED, dstIp, dstPort, socketId));
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " Cannot connect receive socket is null ");
        }

    }

    public void tcpSend(int socketId, Serializable data)
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
            }
            catch (IOException e)
            {
                String dstIp = NetworkUtils.getSocketDistantIp(socket);
                int dstPort = NetworkUtils.getSocketDistantPort(socket);

                Log.Error(this.getClass().getName() + " Error in TCP Send");

                this.eventManager.notiy(new ConnectionEvent(CONNECTION_STATUS.FAILED, dstIp, dstPort, socketId));
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " Cannot send socket is null ");
        }
    }

    public void connect(int socketId, String ip, int port)
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

                String srcIp = NetworkUtils.getSocketLocalIp(socket);
                int srcPort = NetworkUtils.getSocketLocalPort(socket);

                Log.Info(this.getClass().getName() + " Connection success with : " + ip + ":" + port + " --> socket id : " + socketId);
                this.eventManager.notiy(new ConnectionEvent(CONNECTION_STATUS.SUCCESS, ip, port, srcIp, srcPort, socketId));
            }
            catch (IOException e)
            {
                Log.Error(this.getClass().getName() + " Cannot connect to : " + ip + ":" + port);
                Log.Print(this.getClass().getName() + " Removing socket with id : " + socketId);

                this.sockets.remove(socketId);
                this.eventManager.notiy(new ConnectionEvent(CONNECTION_STATUS.FAILED, ip, port, socketId));
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " Cannot connect to : " + ip + ":" + port + " socket is null");
            this.eventManager.notiy(new ConnectionEvent(CONNECTION_STATUS.FAILED, ip, port, socketId));
        }
    }

    public void startTcpServer(int serverId, int port)
    {
        ServerSocket server =  this.tcpServers.get(serverId);

        if (server != null)
        {
            Log.Info(this.getClass().getName() + " Start TCP server on port : " + port);

            try
            {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
                server.bind(inetSocketAddress);

                while (!server.isClosed())
                {
                    Socket socket = server.accept();

                    int socketId = this.sockets.add(socket);

                    String srcIp = NetworkUtils.getSocketLocalIp(socket);
                    String dstIp = NetworkUtils.getSocketDistantIp(socket);

                    int srcPort = NetworkUtils.getSocketLocalPort(socket);
                    int dstPort = NetworkUtils.getSocketDistantPort(socket);

                    Log.Info(this.getClass().getName() + " New client : " + dstIp + ":" + dstPort);
                    this.eventManager.notiy(new ConnectionEvent(ConnectionEvent.CONNECTION_STATUS.SUCCESS, dstIp, dstPort, srcIp, srcPort, socketId));
                }
            }
            catch (IOException e)
            {
                Log.Error(this.getClass().getName() + " Error in TCP TCP server");
            }
        }
        else
        {
            Log.Error(this.getClass().getName() + " TCP server port : " + port + " is null ");
        }
    }

    public void startUdpServer(int serverId, int port) throws IOException
    {
        DatagramSocket server =  this.udpServers.get(serverId);

        if (server != null)
        {
            Log.Info(this.getClass().getName() + " Start UDP server on port : " + port);

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            server.bind(inetSocketAddress);

            while (!server.isClosed())
            {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, bufferSize);
                server.receive(datagramPacket);
            };
        }
        else
        {
            Log.Error(this.getClass().getName() + " TCP server port : " + port + "is null ");
        }
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
            Log.Error(this.getClass().getName() + " No TCP server with id : " + serverID);
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
            Log.Error(this.getClass().getName() + " Cannot close socket is null ");
        }
    }
}