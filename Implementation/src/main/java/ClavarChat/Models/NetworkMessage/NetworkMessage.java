package ClavarChat.Models.NetworkMessage;

import ClavarChat.Models.NetworkMessage.Enums.NETWORK_MESSAGE_TYPE;
import java.io.Serializable;

public class NetworkMessage implements Serializable
{
    public NETWORK_MESSAGE_TYPE type;

    public NetworkMessage(NETWORK_MESSAGE_TYPE type)
    {
        this.type = type;
    }
}
