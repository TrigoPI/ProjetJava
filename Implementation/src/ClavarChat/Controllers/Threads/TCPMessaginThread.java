package ClavarChat.Controllers.Threads;

import java.io.IOException;
import java.net.Socket;

public class TCPMessaginThread extends NetworkThread
{
    protected Socket socket;

    protected TCPMessaginThread()
    {
        super();
        this.socket = null;
    }

    protected TCPMessaginThread(Socket socket)
    {
        super();
        this.socket = socket;
    }

    @Override
    void close()
    {
        try
        {
            this.socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
