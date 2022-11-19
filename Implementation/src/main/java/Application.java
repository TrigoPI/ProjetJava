import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Paquets.Enums.PAQUET_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Log.Log;

import java.util.Scanner;

public class Application
{
    public static void main(String[] args)
    {
        Log.off();

        NetworkManager networkManager = new NetworkManager(4000, 5000);
        networkManager.startTCPServer();
        networkManager.startUDPServer();


        while (true)
        {
            Scanner scanner = new Scanner(System.in);
            String msg = scanner.nextLine();
            networkManager.sendTCP(
                    new Paquet(
                            new UserData("Alexis", msg),
                            PAQUET_TYPE.PAQUET_LOGIN,
                            "192.168.1.2"
                    )
            );
        }
    }
}
