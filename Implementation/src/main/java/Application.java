import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Paquets.Enums.PAQUET_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Models.Users.UserData;
import ClavarChat.Utils.Servers.TCPServerTest;

public class Application
{
    public static void main(String[] args)
    {
        NetworkManager networkManager = new NetworkManager();

        new TCPServerTest(4000);

        Paquet paquet = new Paquet(new UserData("Alexis", "0000", "127.0.0.1"), PAQUET_TYPE.PAQUET_LOGIN);
        networkManager.sendTCP(paquet);
    }
}
