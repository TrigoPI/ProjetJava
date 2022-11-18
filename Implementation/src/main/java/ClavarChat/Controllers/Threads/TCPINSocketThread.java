package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.DataEvent;
import ClavarChat.Models.Events.EndConnectionEvent;
import ClavarChat.Models.Events.Enums.THREAD_EVENT_TYPE;
import ClavarChat.Models.Events.ThreadEvent;
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

    public void receive(Paquet paquet)
    {
        Log.Print(this.getClass().getName() + " : " + this.getLocalIP() + ":" + this.getLocalPort() + " <-- " + this.getDistantIP() + ":" + this.getDistantPort());
        this.eventManager.notiy(new DataEvent(paquet));
    }

    @Override
    public void run()
    {
        Log.Info(this.getClass().getName() + " : " + this.getLocalIP() + ":" + this.getLocalPort() + " <-- " + this.getDistantIP() + ":" + this.getDistantPort());

        this.update();
        this.eventManager.notiy(new EndConnectionEvent(this.distantIP.toString().split("/")[1]));
        this.eventManager.notiy(new ThreadEvent(THREAD_EVENT_TYPE.THREAD_EVENT_FINISHED, this.getIdString()));

        Log.Info(this.getClass().getName() + " : " + this.getLocalIP() + ":" + this.getLocalPort() + " <-- " + this.getDistantIP() + ":" + this.getDistantPort() + " finished");
    }

    private void update()
    {
        try
        {
            this.running = true;
            InputStream in = socket.getInputStream();
            ObjectInputStream iin = new ObjectInputStream(in);
            while (this.running) this.receive((Paquet)iin.readObject());
        }
        catch (IOException | ClassNotFoundException e)
        {
            Log.Warning(this.getClass().getName() + " ERROR : " + this.getLocalIP() + ":" + this.getLocalPort() + " <-- " + this.getDistantIP() + ":" + this.getDistantPort());

        }

        this.running = false;
    }
}