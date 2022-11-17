package ClavarChat.Utils.Servers;

import ClavarChat.Models.Paquets.Enums.PAQUET_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Loggin.Loggin;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
