import ClavarChat.ClavarChatAPI;
import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Events.Enums.NETWORK_EVENT_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Models.Paquets.Enums.PAQUET_TYPE;
import ClavarChat.Models.Users.UserData;

public class Application
{
    public static void main(String[] args)
    {
        NetworkManager networkManager = new NetworkManager(4000, 5000);
        networkManager.sendTCP(new Paquet(new UserData("alexis", "0000", "192.168.1.2"), PAQUET_TYPE.PAQUET_LOGIN));
    }
}
