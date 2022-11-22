package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.PaquetEvent;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServerThread extends ServerThread
{
    private DatagramSocket datagramSocket;

    public UDPServerThread(int port)
    {
        super(port);
    }

    @Override
    protected void runServer()
    {
        Log.Info(this.getClass().getName() + " start on : " + this.port);
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        while (true)
        {
            try
            {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, bufferSize);
                this.datagramSocket.receive(datagramPacket);
                this.receive(datagramPacket);
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    @Override
    protected void createServer(int port)
    {
        try
        {
            this.datagramSocket = new DatagramSocket(port);
        }
        catch (SocketException e) { e.printStackTrace(); }
    }

    private void receive(DatagramPacket datagramPacket)
    {
        Log.Info(this.getClass().getName() + " paquet from UDP");

        try
        {
            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));

            Serializable data = (Serializable)iStream.readObject();
            String src = NetworkUtils.inetAddressToString(datagramPacket.getAddress());
            int port = datagramPacket.getPort();
            this.eventManager.notiy(new PaquetEvent(data, src, port));
        }
        catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
    }
}
