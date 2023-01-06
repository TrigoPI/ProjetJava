package ClavarChat.Models.ClvcSocket;

import ClavarChat.Controllers.Managers.Network.NetworkManager;
import ClavarChat.Models.NetworkPaquet.NetworkPaquet;
import ClavarChat.Utils.Log.Log;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ClvcSocket
{
    public enum SOCKET_STATE { IDLE, CONNECTING, CONNECTED, CLOSE_WAIT, CLOSED };

    private final AtomicReference<SOCKET_STATE> state;

    private int srcPort;
    private String srcIp;
    private final int socketId;
    private final int dstPort;
    private final String dstIp;

    private final LinkedBlockingQueue<Serializable> buffer;

    private final NetworkManager networkManager;

    public ClvcSocket(int socketId, String srcIp, int srcPort, String dstIp, int dstPort, NetworkManager networkManager)
    {
        this.buffer = new LinkedBlockingQueue<>();
        this.state = new AtomicReference<>(SOCKET_STATE.CONNECTED);
        this.networkManager = networkManager;
        this.socketId = socketId;
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
    }

    public ClvcSocket(int socketId, String dstIp, int dstPort, NetworkManager networkManager)
    {
        this.buffer = new LinkedBlockingQueue<>();
        this.state = new AtomicReference<>(SOCKET_STATE.IDLE);
        this.networkManager = networkManager;
        this.socketId = socketId;
        this.srcIp = null;
        this.srcPort = -1;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
    }

    public int getLocalPort()
    {
        return this.networkManager.getLocalSocketPort(this.socketId);
    }

    public int getDistantPort()
    {
        return this.networkManager.getDistantSocketPort(this.socketId);
    }

    public int getSocketId()
    {
        return socketId;
    }

    public int getSrcPort()
    {
        return srcPort;
    }

    public int getDstPort()
    {
        return dstPort;
    }

    public boolean isClosed()
    {
        return this.state.get() == SOCKET_STATE.CLOSED;
    }

    public String getSrcIp()
    {
        return srcIp;
    }

    public String getDstIp()
    {
        return dstIp;
    }

    public String getLocalIp()
    {
        return this.networkManager.getLocalSocketIp(this.socketId);
    }

    public String getDistantIp()
    {
        return this.networkManager.getDistantSocketIp(this.socketId);
    }

    public SOCKET_STATE getState()
    {
        return this.state.get();
    }

    public int connect()
    {
        this.state.set(SOCKET_STATE.CONNECTING);
        int code = this.networkManager.connect(this.socketId, this.dstIp, this.dstPort);

        if (code < 0)
        {
            this.state.set(SOCKET_STATE.CLOSED);
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ]" + " Connection failed with : " + this.dstIp + ":" + this.dstPort);
            return code;
        }

        if (this.state.get() != SOCKET_STATE.CLOSE_WAIT) this.state.set(SOCKET_STATE.CONNECTED);

        this.srcIp   = this.networkManager.getLocalSocketIp(this.socketId);
        this.srcPort = this.networkManager.getLocalSocketPort(this.socketId);

        return code;
    }

    public NetworkPaquet receive()
    {
        if (this.state.get() == SOCKET_STATE.IDLE)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot receive data, socket is IDLE");
            return null;
        }

        if (this.state.get() == SOCKET_STATE.CONNECTING)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot receive data, socket is CONNECTING");
            return null;
        }

        if (this.state.get() == SOCKET_STATE.CLOSE_WAIT)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot receive data, socket is CLOSE_WAIT");
            return null;
        }

        if (this.state.get() == SOCKET_STATE.CLOSED)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot receive data, socket is CLOSED");
            return null;
        }

        Log.Info(this.getClass().getName() + "[ socket : " + this.socketId + " ] " + " Waiting for data : " + this.srcIp + ":" + this.srcPort + " <-- " + this.dstIp + ":" + this.dstPort);
        NetworkPaquet data = this.networkManager.tcpReceive(this.socketId);

        if (data == null)
        {
            Log.Error(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Error in socket " + this.socketId);
            this.state.set(SOCKET_STATE.CLOSED);
            return null;
        }

        Log.Info(this.getClass().getName() + "[ socket : " + this.socketId + " ] " + " Receiving data : " + this.srcIp + ":" + this.srcPort + " <-- " + this.dstIp + ":" + this.dstPort);
        return data;
    }

    public void send()
    {
        if (this.state.get() == SOCKET_STATE.IDLE)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot send data, socket is IDLE");
            return;
        }

        if (this.state.get() == SOCKET_STATE.CONNECTING)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot send data, socket is CONNECTING");
            return;
        }

        if (this.state.get() == SOCKET_STATE.CLOSED)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot send data, socket is CLOSED");
            return;
        }

        while (!this.buffer.isEmpty())
        {
            Log.Print(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " send data to " + this.getDistantIp() + ":" + this.getDistantPort() + ", buffer size : " + this.buffer.size());
            this.networkManager.tcpSend(this.socketId, this.buffer.poll());
        }

        if (this.state.get() == SOCKET_STATE.CLOSE_WAIT)
        {
            Log.Info(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Closing socket " + this.socketId);
            this.state.set(SOCKET_STATE.CLOSED);
            this.networkManager.closeTcpSocket(this.socketId);
        }
    }

    public void put(Serializable data)
    {
        if (this.state.get() == SOCKET_STATE.CLOSE_WAIT)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot put data, socket is CLOSE_WAIT");
            return;
        }

        if (this.state.get() == SOCKET_STATE.CLOSED)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot put data, socket is CLOSED");
            return;
        }

        this.buffer.add(data);
    }

    public void forceClose()
    {
        this.state.set(SOCKET_STATE.CLOSED);
        this.networkManager.closeTcpSocket(this.socketId);
    }

    public void close()
    {
        if (this.state.get() == SOCKET_STATE.CLOSED)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " socket already CLOSED");
            return;
        }

        if (this.state.get() == SOCKET_STATE.IDLE)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot close socket IDLE");
            return;
        }

        if (this.state.get() == SOCKET_STATE.CLOSE_WAIT)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot close socket CLOSE_WAIT");
            return;
        }

        if (this.state.get() == SOCKET_STATE.CONNECTING)
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Cannot close socket CONNECTING --> CLOSE_WAIT");
            this.state.set(SOCKET_STATE.CLOSE_WAIT);
            return;
        }

        if (!this.buffer.isEmpty())
        {
            Log.Warning(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Socket id " + this.socketId + " buffer not empty " + this.state.get() + " --> CLOSE_WAIT");
            this.state.set(SOCKET_STATE.CLOSE_WAIT);
            return;
        }

        Log.Info(this.getClass().getName() + " [ socket : " + this.socketId + " ] " + " Closing socket " + this.socketId);
        this.state.set(SOCKET_STATE.CLOSED);
        this.networkManager.closeTcpSocket(this.socketId);
    }
}
