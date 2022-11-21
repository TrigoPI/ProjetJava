package Application.ClavaChatCLI;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Utils.CLI.CLI;
import ClavarChat.Utils.CLI.Modules.ModuleLogsCLI;

public class ClavarChatCLI
{
    public static void run()
    {
        CLI.createCLI();

        NetworkManager networkManager = new NetworkManager(4000, 5000);
        networkManager.startTCPServer();
        networkManager.startUDPServer();

        ModuleLogsCLI moduleLogsCLI = new ModuleLogsCLI();

        CLI.installModule("log", moduleLogsCLI);
        CLI.start();
    }
}
