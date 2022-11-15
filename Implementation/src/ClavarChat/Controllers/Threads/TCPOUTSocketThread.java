package ClavarChat.Controllers.Threads;

import java.net.Socket;

public class TCPOUTSocketThread extends TCPMessaginThread
{
    public TCPOUTSocketThread(String ip)
    {
        super();
        this.start();
    }

    public TCPOUTSocketThread(Socket socket)
    {
        super(socket);
        this.start();
    }

    @Override
    public void run()
    {

    }
}
