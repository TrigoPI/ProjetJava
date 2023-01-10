package GUI.GUIControllers.Controllers;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.GUI.Component.Avatar.Avatar;
import ClavarChat.Utils.Log.Log;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable
{
    private final ClavarChatAPI api;

    private final Avatar avatar;
    @FXML
    private HBox modifpage;
    @FXML
    private MFXTextField pseudoField;
    @FXML
    private Label pseudoLABEL;
    @FXML
    private Label userIDLABEL;
    @FXML
    private MFXButton avatarButton;
    @FXML
    private MFXButton returnButton;

    public SettingsController(ClavarChatAPI api)
    {
        this.api = api;
        Image image = new Image("file:/Users/clementroussel/Desktop/Général/wp.jpeg");
        this.avatar = new Avatar(image, 50, true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");
        this.modifpage.getChildren().add(0,avatar);
        String pseudo = this.api.getPseudo();
        int userID = this.api.getId();
        this.pseudoLABEL.setText(pseudo);
        this.userIDLABEL.setText("#"+userID);

    }

    @FXML
    private void pseudoModifyer()
    {
        int userID = this.api.getId();
        String newPseudo = this.pseudoField.getText();
        this.api.updateUser(userID,newPseudo);
        this.pseudoLABEL.setText(newPseudo);
        this.pseudoField.setText("");
    }

    @FXML
    private void avatarModifyer() throws IOException
    {
        int userID = this.api.getId();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(this.avatarButton.getScene().getWindow());
        //FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = FileUtils.readFileToByteArray(file);
        Image img = new Image(new ByteArrayInputStream(buffer));
        this.api.updateAvatar(userID,buffer);
        this.avatar.changeImage(img);
    }
    @FXML
    private void retourChat()
    {
        System.out.println("OOOOOOOOOOOOK");
    }
}