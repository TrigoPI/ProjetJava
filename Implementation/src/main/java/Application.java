import ClavarChat.ClavarChatAPI;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Models.Paquets.Enums.PAQUET_TYPE;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;

public class Application
{
    public static void main(String[] args)
    {
        Log.on();

        NetworkManager networkManager = new NetworkManager(4000, 5000);
        //networkManager.startTCPServer();

//        while (true)
//        {
//            Scanner scanner = new Scanner(System.in);
//            System.out.print("> ");
//            String msg = scanner.nextLine();
//            networkManager.sendTCP(new Paquet(new UserData(msg, "0000", "192.168.1.2"), PAQUET_TYPE.PAQUET_LOGIN));
//        }

            networkManager.sendTCP(new Paquet(new UserData("test", "0000", "192.168.1.2"), PAQUET_TYPE.PAQUET_LOGIN));
    }
}
