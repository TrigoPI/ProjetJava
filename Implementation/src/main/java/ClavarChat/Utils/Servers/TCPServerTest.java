package ClavarChat.Utils.Servers;

import ClavarChat.Utils.Log.Log;
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
        Log.Info("Start srv");

        while (true)
        {
            try
            {
                Socket socket = this.srv.accept();
                Log.Info("new client");

                /*Paquet paquet = new Paquet(new UserData("alexis", "0000", "127.0.0.1"), PAQUET_TYPE.PAQUET_LOGIN);

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(paquet);*/
                //socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
