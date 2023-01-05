package GUI.GUIControllers.Controllers;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.Log.Log;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable
{
    private final ClavarChatAPI api;

    public SettingsController(ClavarChatAPI api)
    {
        this.api = api;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");
    }

}
