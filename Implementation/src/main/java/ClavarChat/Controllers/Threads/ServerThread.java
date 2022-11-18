package ClavarChat.Controllers.Threads;

public abstract class ServerThread extends NetworkThread
{
    protected int port;

    protected ServerThread(int port)
    {
        this.port = port;
        this.createServer(port);
    }

    protected abstract void createServer(int port);
}
