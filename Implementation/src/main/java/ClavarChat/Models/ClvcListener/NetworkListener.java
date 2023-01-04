package ClavarChat.Models.ClvcListener;

import ClavarChat.Models.ClvcMessage.ClvcMessage;

public interface NetworkListener
{
    void onFinishedSending(int socketId, String dstIp);
    void onNewConnection(int socketId, String srcIp, int srcPort, String dstIp, int dstPort);
    void onConnectionSuccess(String dstIp);
    void onConnectionFailed(int socketId, String dstIp);
    void onPacket(String srcIp, int srcPort, String dstIp, int dstPort, ClvcMessage data);
}
