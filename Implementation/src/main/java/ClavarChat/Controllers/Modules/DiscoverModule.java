package ClavarChat.Controllers.Modules;

import ClavarChat.Controllers.Managers.NetworkManager;
import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;
import ClavarChat.Models.ClavarChatMessage.Enums.MESSAGE_TYPE;
import ClavarChat.Models.ClavarChatMessage.UserInformationMessage;
import ClavarChat.Utils.Log.Log;

import java.util.ArrayList;

public class DiscoverModule
{
    private NetworkManager networkManager;
    private int userCount;
    private int responseCount;


    public DiscoverModule(NetworkManager networkManager)
    {
        this.networkManager = networkManager;
        this.userCount = 0;
        this.responseCount = -1;
    }

    private void broadcast()
    {
        ArrayList<String> broadcast = this.networkManager.getBroadcastAddresses();
        for (String addresse : broadcast) this.networkManager.sendUDP(new ClavarChatMessage(MESSAGE_TYPE.DISCOVER), addresse);
    }

    private void waitResponses()
    {
        Log.Print(this.getClass().getName() + "Waiting...");

        while (this.responseCount < this.userCount)
        {

        }
    }

    public void discover()
    {
        this.broadcast();
        this.waitResponses();
    }

    public void onUserInformation(UserInformationMessage data)
    {
        if (this.responseCount == -1)
        {
            Log.Print(this.getClass().getName() + " Waiting for " + data.userCount + " responses");

            this.responseCount = 1;
            this.userCount = data.userCount;
        }
    }
}
