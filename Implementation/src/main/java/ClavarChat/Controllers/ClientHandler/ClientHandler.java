package ClavarChat.Controllers.ClientHandler;

import ClavarChat.Controllers.Threads.TCPINSocketThread;
import ClavarChat.Controllers.Threads.TCPOUTSocketThread;

public class ClientHandler
{
    public TCPINSocketThread in;
    public TCPOUTSocketThread out;

    public ClientHandler(TCPINSocketThread in, TCPOUTSocketThread out)
    {
        this.in = in;
        this.out = out;
    }

    public ClientHandler()
    {
        this.in = null;
        this.out = null;
    }

    public void setIn(TCPINSocketThread in)
    {
        this.in = in;
    }

    public void setOut(TCPOUTSocketThread out)
    {
        this.out = out;
    }
}
