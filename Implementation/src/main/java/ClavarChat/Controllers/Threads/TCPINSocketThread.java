package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.DataEvent;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Log.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TCPINSocketThread extends TCPMessaginThread
{
    public TCPINSocketThread(Socket socket)
    {
        super(socket);
    }

    @Override
    public void run()
    {
        Log.Info("TCP_IN RUN : " + this.getLocalIP() + ":" + this.localPort + " <-- " + this.getDistantIP() + ":" + this.getDistantPort());
        try
        {
            InputStream in = socket.getInputStream();
            ObjectInputStream iin = new ObjectInputStream(in);
            this.receive((Paquet)iin.readObject());
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void receive(Paquet paquet)
    {
        Log.Print("Receive Data : " + this.getLocalIP() + ":" + this.localPort + " <-- " + this.getDistantIP() + ":" + this.getDistantPort());
        this.eventManager.notiy(new DataEvent(paquet));
    }
}