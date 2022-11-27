package ClavarChat.Controllers.ClavarChatNetwork.Runnable;

import ClavarChat.Controllers.Managers.NetworkManager;

import java.io.IOException;

public class TCPIN extends TcpMessagin
{

    public TCPIN(NetworkManager networkManager, int socketID)
    {
        super(networkManager, socketID);
    }

    @Override
    protected void runSocket()
    {
        try
        {
            while (true)
            {
                this.networkManager.tcpReceive(this.socketId);
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
