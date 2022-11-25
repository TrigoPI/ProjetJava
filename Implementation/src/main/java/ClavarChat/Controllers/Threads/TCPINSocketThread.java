package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.SocketDataEvent;
import ClavarChat.Utils.Log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
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
            while (this.running) this.receive((Serializable) iin.readObject());
        }
        catch (IOException | ClassNotFoundException e) { Log.Warning(this.getClass().getName() + " ERROR : " + this.localIP + ":" + this.localPort + " <-- " + this.distantIP + ":" + this.distantPort); }

        Log.Info(this.getClass().getName() + " : " + this.localIP + ":" + this.localPort + " <-- " + this.distantIP + ":" + this.distantPort + " finished");
    }

    private void receive(Serializable data)
    {
        Log.Print(this.getClass().getName() + " receive : " + this.localIP + ":" + this.localPort + " <-- " + this.distantIP + ":" + this.distantPort);
        this.eventManager.notiy(new SocketDataEvent(this.distantIP, this.distantPort, data));
    }
}