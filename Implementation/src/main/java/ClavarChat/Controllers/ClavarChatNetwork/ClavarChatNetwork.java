package ClavarChat.Controllers.ClavarChatNetwork;

import ClavarChat.Controllers.ClavarChatNetwork.Runnable.*;
import ClavarChat.Controllers.Listenner.Listener;
import ClavarChat.Controllers.Managers.EventManager;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Controllers.Managers.ThreadManager;
import ClavarChat.Models.Events.ConnectionEvent;
import ClavarChat.Models.Events.Event;
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.CLI.Modules.ModuleCLI;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.PackedArray.PackedArray;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class ClavarChatNetwork implements Listener
{
    private EventManager eventManager;

    private ThreadManager threadManager;
    private NetworkManager networkManager;

    private HashMap<String, LinkedList<Serializable>> pendingDatas;
    private HashMap<String, Integer[]> clientsMap;
    private PackedArray<TCPIN> tcpIn;
    private PackedArray<TCPOUT> tcpOut;

    private int tcpServerID;
    private int udpServerID;

    public ClavarChatNetwork(ThreadManager threadManager, NetworkManager networkManager)
    {
        this.eventManager = EventManager.getInstance();

        this.threadManager = threadManager;
        this.networkManager = networkManager;

        this.tcpServerID = this.networkManager.createTcpServer();
        this.udpServerID = this.networkManager.createUdpServer();

        this.pendingDatas = new HashMap<>();
        this.clientsMap = new HashMap<>();

        this.tcpIn = new PackedArray<>();
        this.tcpOut = new PackedArray<>();

        this.eventManager.addListenner(this, Event.EVENT_TYPE.EVENT_NETWORK_CONNECTION);

        this.DEBUG();
    }

    private void DEBUG()
    {
        ModuleCLI moduleCLI = new ModuleCLI();

        moduleCLI.addCommand("send", () -> {
            String ip = moduleCLI.getUserInput("IP : ");
            this.send(ip, 5000, new String("OOOOOOKKKKK"));
        });

        CLI.installModule("network", moduleCLI);
    }

    public void close(String ip)
    {

    }

    public void send(String ip, int port, Serializable data)
    {
        if (this.clientsMap.containsKey(ip))
        {
            int outId = this.clientsMap.get(ip)[1];
            TCPOUT out = this.tcpOut.get(outId);
            out.put(data);
        }
        else
        {
            if (!this.pendingDatas.containsKey(ip))
            {
                this.pendingDatas.put(ip, new LinkedList<>());
                int socketId = this.networkManager.createSocket();
                int threadId = this.threadManager.createThread(new TcpConnection(this.networkManager, socketId, ip, port));
                this.threadManager.startThread(threadId);
            }
            this.pendingDatas.get(ip).push(data);
        }
    }

    public void startServer()
    {
        int tcpThreadID = this.threadManager.createThread(new TcpServer(this.networkManager, this.tcpServerID, 5000));
        int udpThreadID = this.threadManager.createThread(new UdpServer(this.networkManager, this.udpServerID, 4000));

        this.threadManager.startThread(tcpThreadID);
        this.threadManager.startThread(udpThreadID);
    }

    @Override
    public void onEvent(Event event)
    {
        switch (event.type)
        {
            case EVENT_NETWORK_CONNECTION:
                this.onNetworkConnectionEvent((ConnectionEvent)event);
                break;
        }
    }

    private void onNetworkConnectionEvent(ConnectionEvent event)
    {
        switch (event.status)
        {
            case SUCCESS:
                this.connectionSuccess(event.socketID, event.distantIP, event.distantPort);
                break;
            case FAILED:
                this.connectionFailed(event.socketID, event.distantIP, event.distantPort);
                break;
            case ENDED:
                break;
        }
    }

    private void connectionSuccess(int socketId, String dstIp, int dstPort)
    {
        Log.Info(this.getClass().getName() + " Connection success with : " + dstIp + ":" + dstPort + " --> socketId " + socketId);
        Log.Print(this.getClass().getName() + " Creating TCP IN/OUT thread");

        int inId = this.threadManager.createThread();
        int outId = this.threadManager.createThread();

        Integer ids[] = new Integer[2];
        TCPIN in = new TCPIN(this.networkManager, socketId);
        TCPOUT out = new TCPOUT(this.networkManager, socketId);

        ids[0] = this.tcpIn.add(in);
        ids[1] = this.tcpOut.add(out);

        this.threadManager.setThreadRunnable(inId, in);
        this.threadManager.setThreadRunnable(outId, out);

        this.threadManager.startThread(inId);
        this.threadManager.startThread(outId);

        this.clientsMap.put(dstIp, ids);

        Log.Print(this.getClass().getName() + " TCPIN id : " + ids[0]);
        Log.Print(this.getClass().getName() + " TCPOUT id : " + ids[1]);

        this.flushPendingData(dstIp);
    }

    private void connectionFailed(int socketId, String dstIp, int dstPort)
    {
        Log.Print(this.getClass().getName() + " Removing pending data to : " + dstIp + ":" + dstPort);
        this.pendingDatas.remove(dstIp);
    }

    private void flushPendingData(String ip)
    {
        if (this.pendingDatas.containsKey(ip))
        {
            int outId = this.clientsMap.get(ip)[1];
            TCPOUT out = this.tcpOut.get(outId);
            LinkedList<Serializable> list = this.pendingDatas.get(ip);

            Log.Print(this.getClass().getName() + " Flushing data to TCP Out : " + outId + " --> " + ip);

            while (!list.isEmpty()) out.put(list.removeLast());
        }
    }
}