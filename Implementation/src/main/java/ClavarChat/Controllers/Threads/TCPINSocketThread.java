package ClavarChat.Controllers.Threads;

import ClavarChat.Utils.Loggin.Loggin;

import java.net.Socket;

public class TCPINSocketThread extends TCPMessaginThread
{
    public TCPINSocketThread(Socket socket)
    {
        super(socket);
    }

    @Override
    public void run()
    {
        Loggin.Info("TCP_IN Socket start");
    }
}
