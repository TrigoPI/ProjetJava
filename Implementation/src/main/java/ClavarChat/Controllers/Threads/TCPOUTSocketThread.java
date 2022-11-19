package ClavarChat.Controllers.Threads;

import ClavarChat.Models.Events.EndConnectionEvent;
import ClavarChat.Models.Events.Enums.THREAD_EVENT_TYPE;
import ClavarChat.Models.Events.ThreadEvent;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Utils.Log.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class TCPOUTSocketThread extends TCPMessaginThread
{
    private LinkedList<Paquet> datas;
    private Semaphore semaphore;

    public TCPOUTSocketThread(Socket socket)
    {
        super(socket);

        this.datas = new LinkedList<Paquet>();
        this.semaphore = new Semaphore(1);
    }

    public void send(Paquet paquet)
    {
        try
        {
            this.semaphore.acquire();
            this.datas.push(paquet);
            this.semaphore.release();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        Log.Info(this.getClass().getName() + " RUN : " + this.getLocalIP() + ":" + this.localPort + " --> " + this.getDistantIP() + ":" + this.getDistantPort());
        this.update();
        this.eventManager.notiy(new EndConnectionEvent(this.distantIP.toString().split("/")[1]));
        this.eventManager.notiy(new ThreadEvent(THREAD_EVENT_TYPE.THREAD_EVENT_FINISHED, this.getIdString()));
        Log.Info(this.getClass().getName() + " : " + this.getLocalIP() + ":" + this.localPort + " --> " + this.getDistantIP() + ":" + this.getDistantPort() + " finished");
    }

    private void update()
    {
        try
        {
            this.running = true;
            OutputStream out = socket.getOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(out);

            while (this.running)
            {
                this.semaphore.acquire();
                if (!this.datas.isEmpty())
                {
                    Log.Info(this.getClass().getName() + " send Data : " + this.getLocalIP() + ":" + this.localPort + " --> " + this.getDistantIP() + ":" + this.getDistantPort());
                    Paquet paquet = this.datas.removeLast();
                    this.semaphore.release();
                    oout.writeObject(paquet);
                }
                this.semaphore.release();
            }
        }
        catch (IOException | InterruptedException e)
        {
            Log.Warning(this.getClass().getName() + " ERROR : " + this.getLocalIP() + ":" + this.localPort + " --> " + this.getDistantIP() + ":" + this.getDistantPort());
        }

        this.running = false;
    }
}
