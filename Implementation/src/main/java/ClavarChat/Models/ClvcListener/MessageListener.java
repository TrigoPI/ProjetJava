package ClavarChat.Models.ClvcListener;

import ClavarChat.Models.ClvcNetworkMessage.ClvcNetworkMessage;

public interface MessageListener
{
    void onData(String srcIp, ClvcNetworkMessage message);
}
