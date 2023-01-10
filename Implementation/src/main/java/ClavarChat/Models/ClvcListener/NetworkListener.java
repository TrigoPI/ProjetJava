package ClavarChat.Models.ClvcListener;

import ClavarChat.Models.ClvcNetworkMessage.ClvcNetworkMessage;

public interface NetworkListener
{
    void onNewConnection(int socketId, String srcIp, int srcPort, String dstIp, int dstPort);
    void onConnectionSuccess(String dstIp);
    void onConnectionFailed(int socketId, String dstIp);
    void onMessengerFinished(String dstIp);
    void onPacket(String from, int tcpPort, ClvcNetworkMessage data);
}
