package ClavarChat.Controllers.Threads;

public abstract class ServerThread extends NetworkThread
{
    protected int port;

    protected ServerThread(int port)
    {
        this.port = port;
    }

    protected abstract void runServer();
    protected abstract void createServer(int port);

    @Override
    protected void update()
    {
        this.createServer(this.port);
        this.runServer();
    }
}
