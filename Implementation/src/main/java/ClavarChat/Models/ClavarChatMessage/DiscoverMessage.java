package ClavarChat.Models.ClavarChatMessage;

import ClavarChat.Models.Users.User;

public class DiscoverMessage extends ClavarChatMessage
{
    public enum DISCOVER_TYPE { REQUEST, RESPONSE }

    public DISCOVER_TYPE discoverType;
    public int count;

    public DiscoverMessage()
    {
        super(MESSAGE_TYPE.DISCOVER);

        this.discoverType = DISCOVER_TYPE.REQUEST;
        this.count = -1;
    }

    public DiscoverMessage(User user, int count)
    {
        super(user, MESSAGE_TYPE.DISCOVER);

        this.discoverType = DISCOVER_TYPE.RESPONSE;
        this.count = count;
    }
}
