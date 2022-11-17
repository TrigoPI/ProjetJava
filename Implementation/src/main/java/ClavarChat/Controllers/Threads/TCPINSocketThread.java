package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.DataEvent;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Loggin.Loggin;


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
        Loggin.Info("TCP_IN on : " + this.socket.getLocalAddress() + ":" + this.socket.getLocalPort() + " --> " + this.ip + ":" + this.port);
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
        Loggin.Print("Receive : " + this.socket.getLocalAddress() + ":" + this.socket.getLocalPort() + " --> " + this.ip + ":" + this.port);
        this.eventManager.notiy(new DataEvent(paquet));
    }
}