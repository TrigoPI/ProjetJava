package ClavarChat.Controllers.ClientHandler;

import ClavarChat.Controllers.Threads.TCPINSocketThread;
import ClavarChat.Controllers.Threads.TCPOUTSocketThread;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler
{
    public TCPINSocketThread in;
    public TCPOUTSocketThread out;
    public Socket socket;

    public ClientHandler(Socket socket, TCPINSocketThread in, TCPOUTSocketThread out)
    {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public ClientHandler()
    {
        this.in = null;
        this.out = null;
    }

    public void stop()
    {
        try
        {
            this.socket.close();
            this.in.stopSocket();
            this.out.stopSocket();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
