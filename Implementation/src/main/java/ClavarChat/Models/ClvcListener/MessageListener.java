package ClavarChat.Models.ClvcListener;

import ClavarChat.Models.ClvcMessage.ClvcMessage;

public interface MessageListener
{
    void onData(String dstIp, ClvcMessage message);
}
