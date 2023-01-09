package GUI.GUIControllers.Controllers;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.GUI.Component.Avatar.Avatar;
import ClavarChat.Utils.Log.Log;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import ClavarChat.Controllers.API.DataBaseAPI.DataBaseAPI;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable
{
    private final ClavarChatAPI api;

    @FXML
    private HBox modifpage;
    @FXML
    private MFXTextField pseudoField;
    @FXML
    private Label pseudoLABEL;
    @FXML
    private Label userIDLABEL;
    @FXML
    private void pseudoModifyer()
    {
        int userID = this.api.getId();
        String newPseudo = this.pseudoField.getText();
        this.api.updateUser(userID,newPseudo);
        this.pseudoLABEL.setText(newPseudo);
    }
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
        String pseudo = this.api.getPseudo();
        int userID = this.api.getId();
        this.pseudoLABEL.setText(pseudo);
        this.userIDLABEL.setText("#"+userID);

    }

}
