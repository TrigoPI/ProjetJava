package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.PaquetEvent;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.NetworkUtils.NetworkUtils;


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

    protected void loop()
    {
        Log.Info(this.getClass().getName() + " RUN : " + this.localIP + ":" + this.localPort + " <-- " + this.distantIP + ":" + this.distantPort);

        try
        {
            InputStream in = socket.getInputStream();
            ObjectInputStream iin = new ObjectInputStream(in);
            while (this.running) this.receive((Paquet)iin.readObject());
        }
        catch (IOException | ClassNotFoundException e) { Log.Warning(this.getClass().getName() + " ERROR : " + this.localIP + ":" + this.localPort + " <-- " + this.distantIP + ":" + this.distantPort); }

        Log.Info(this.getClass().getName() + " : " + this.localIP + ":" + this.localPort + " <-- " + this.distantIP + ":" + this.distantPort + " finished");
    }

    private void receive(Paquet paquet)
    {
        Log.Print(this.getClass().getName() + " receive : " + this.localIP+ ":" + this.localPort + " <-- " + this.distantIP + ":" + this.distantPort);
        paquet.src = NetworkUtils.inetAddressToString(socket.getInetAddress());
        this.eventManager.notiy(new PaquetEvent(paquet));
    }
}