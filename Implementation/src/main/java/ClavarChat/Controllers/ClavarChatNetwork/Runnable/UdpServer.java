package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Events.SocketDataEvent;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;

public class UdpServer extends Server
{
    public UdpServer(NetworkManager networkManager, int serverId, int port)
    {
        super(networkManager, serverId, port);
    }

    @Override
    protected void runServer()
    {
        int code = this.networkManager.startUdpServer(this.serverId, port);

        if (code == 0)
        {
            while (true)
            {
                DatagramPacket datagramPacket = this.networkManager.udpReceive(this.serverId);

                if (datagramPacket != null)
                {
                    try
                    {
                        ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
                        Serializable data = (Serializable)iStream.readObject();
                        String dstIp = NetworkUtils.inetAddressToString(datagramPacket.getAddress());
                        int port = datagramPacket.getPort();

                        this.eventManager.notiy(new SocketDataEvent(dstIp, port, data));
                    }
                    catch (IOException | ClassNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
