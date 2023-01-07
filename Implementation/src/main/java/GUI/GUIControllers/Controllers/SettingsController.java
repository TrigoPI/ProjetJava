package GUI.GUIControllers.Controllers;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.GUI.Component.Avatar.Avatar;
import ClavarChat.Utils.Log.Log;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable
{
    private final ClavarChatAPI api;

    @FXML
    private HBox modifpage;
    private MFXTextField pseudoField;

    public SettingsController(ClavarChatAPI api)
    {
        this.api = api;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");
        Image image = new Image("file:/Users/clementroussel/Desktop/Général/wp.jpeg");
        Avatar avatar = new Avatar(image, 50, true);
        this.modifpage.getChildren().add(0,avatar);
    }

}