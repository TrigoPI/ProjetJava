package ClavarChat.Models.ClavarChatListener;

import ClavarChat.Models.ClavarChatMessage.ClavarChatMessage;

public interface NetworkListener
{
    void onFinishedSending(int socketId, String dstIp);
    void onNewConnection(int socketId, String srcIp, int srcPort, String dstIp, int dstPort);
    void onConnectionSuccess(String dstIp);
    void onConnectionFailed(int socketId, String dstIp);
    void onData(String srcIp, int srcPort, String dstIp, int dstPort, ClavarChatMessage data);
}
