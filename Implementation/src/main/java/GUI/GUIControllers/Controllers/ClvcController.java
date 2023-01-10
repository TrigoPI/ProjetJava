package GUI.GUIControllers.Controllers;

import ClavarChat.ClavarChatAPI;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class ClvcController implements Initializable
{
    protected final ClavarChatAPI api;

    public ClvcController(ClavarChatAPI api)
    {
        this.api = api;
    }

    @Override
    public abstract void initialize(URL url, ResourceBundle resourceBundle);
}
