package ClavarChat.Utils.Servers;

import ClavarChat.Utils.Loggin.Loggin;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerTest extends Thread
{
    private ServerSocket srv;

    public TCPServerTest(int port)
    {
        try
        {
            this.srv = new ServerSocket(port);
            this.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        Loggin.Info("Start srv");

        while (true)
        {
            try
            {
                Socket socket = this.srv.accept();
                Loggin.Info("new client");
                socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
