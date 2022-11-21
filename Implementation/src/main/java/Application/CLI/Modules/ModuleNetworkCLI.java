package Application.CLI.Modules;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.Paquets.Enums.PAQUET_TYPE;
import ClavarChat.Models.Paquets.Paquet;
import ClavarChat.Models.Users.UserData;

import java.util.ArrayList;
import java.util.Scanner;

public class ModuleNetworkCLI extends ModuleCLI
{
    private NetworkManager networkManager;

    public ModuleNetworkCLI(NetworkManager networkManager)
    {
        this.networkManager = networkManager;
        this.addCommand("networks");
        this.addCommand("send");
        this.addCommand("close-socket");
        this.addCommand("get-socket");
    }

    @Override
    protected void command(String cmd)
    {
        switch (cmd) {
            case "networks":
                this.networksCmd();
                break;
            case "send":
                this.sendCmd();
                break;
            case "close-socket":
                this.closeSocketCmd();
                break;
            case "get-socket":
                this.getSocketCmd();
                break;
        }
    }

    private String getUserInput(String msg)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print(msg);
        return scanner.nextLine();
    }

    private void networksCmd()
    {
        ArrayList<String> allIp = this.networkManager.getAllIp();
        ArrayList<String> allNetworks = this.networkManager.getConnectedNetworks();
        ArrayList<String> allBroadcasts = this.networkManager.getBroadcastAddresses();

        for (int i = 0; i < allIp.size(); i++)
        {
            String ip = allIp.get(i);
            String network = allNetworks.get(i);
            String broadcast = allBroadcasts.get(i);

            System.out.println("ip : " + ip + " - network : " + network + " - broadcast : " + broadcast);
        }
    }

    private void sendCmd()
    {
        String protocol = this.getUserInput("TCP/UDP : ").toLowerCase();
        String pseudo = this.getUserInput("Pseudo : ");
        String ip = this.getUserInput("IP : ");
        String id = this.getUserInput("ID : ");

        UserData user = new UserData(pseudo, id);
        Paquet paquet = new Paquet(user, PAQUET_TYPE.PAQUET_LOGIN, ip);

        switch (protocol)
        {
            case "tcp":
                this.networkManager.sendTCP(paquet);
                break;
            case "udp":
                this.networkManager.sendUDP(paquet);
                break;
        }
    }

    private void closeSocketCmd()
    {
        this.networkManager.closeAllTcp();
    }

    private void getSocketCmd()
    {
        ArrayList<String[]> sockets = this.networkManager.getActiveSockets();
        for (String[] infos : sockets) System.out.println(infos[0] + ":" + infos[1] + " --> " + infos[2] + ":" + infos[3]);
    }
}
