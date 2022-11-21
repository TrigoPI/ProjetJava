package Application.ClavaChatCLI;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Utils.CLI.CLI;

public class ClavarChatCLI
{
    public static void run()
    {
        CLI.createCLI();

        NetworkManager networkManager = new NetworkManager(4000, 5000);

        CLI.start();
    }
}
