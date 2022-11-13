package ClavarChat;

import ClavarChat.Controllers.Managers.*;
import ClavarChat.Models.Events.*;

import java.io.IOException;

public class ClavarChatAPI implements Listenner
{
    private EventManager eventManager;
    private NetworkManager networkManager;
    private NetworkThreadManager networkThreadManager;

    public ClavarChatAPI() throws IOException
    {
        this.eventManager = new EventManager();
        this.networkThreadManager = new NetworkThreadManager(this.eventManager);
        this.networkManager = new NetworkManager(4000, 5000, this.eventManager, this.networkThreadManager);

        this.initEvent();
    }

    private void initEvent()
    {
        this.eventManager.addEventType(EVENT_TYPE.NETWORK_EVENT);
        this.eventManager.addEventType(EVENT_TYPE.MESSAGE_EVENT);
        this.eventManager.addEventType(EVENT_TYPE.THREAD_EVENT);

        this.eventManager.addListenner(EVENT_TYPE.MESSAGE_EVENT, this);
        this.eventManager.addListenner(EVENT_TYPE.NETWORK_EVENT, this.networkManager);
        this.eventManager.addListenner(EVENT_TYPE.THREAD_EVENT, this.networkManager);
        this.eventManager.addListenner(EVENT_TYPE.THREAD_EVENT, this.networkThreadManager);
    }

    public void login()
    {
        System.out.println("login");
    }

    @Override
    public void onEvent(Event event)
    {
        System.out.println("onEvent API : " + event.type);
    }
}
