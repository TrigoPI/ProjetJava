package GUI.GUIControllers.Controllers;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.BytesImage.BytesImage;
import ClavarChat.Utils.GUI.Component.Avatar.Avatar;
import ClavarChat.Utils.Log.Log;
import Resources.Resources;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends ClvcController
{
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
    private Button returnButton;

    public SettingsController(ClavarChatAPI api)
    {
        super(api);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");

        int userID = this.api.getId();

        BytesImage buffer = this.api.getAvatar();
        Image image = new Image(buffer.toInputStream());
        Avatar avatar = new Avatar(image, 50, true);
        String pseudo = this.api.getPseudo();

        this.modifpage.getChildren().add(0, avatar);
        this.pseudoLABEL.setText(pseudo);
        this.userIDLABEL.setText("#"+userID);

    }

    @Override
    public void onChange()
    {

    }

    @FXML
    private void pseudoModifyer()
    {
        int userID = this.api.getId();

        String newPseudo = this.pseudoField.getText().trim();

        if (newPseudo.isEmpty()) return;

        this.api.updatePseudo(userID, newPseudo);
        this.pseudoLABEL.setText(newPseudo);
        this.pseudoField.setText("");
    }

    @FXML
    private void avatarModifyer() throws IOException
    {
        int userID = this.api.getId();

        ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Select image", "*.png", "*.jpeg", "*.jpg");

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(this.avatarButton.getScene().getWindow());

        if (file == null) return;

        byte[] buffer = FileUtils.readFileToByteArray(file);
        Image img = new Image(new ByteArrayInputStream(buffer));
        Avatar avatar = new Avatar(img, 50, true);

        this.api.updateAvatar(userID, buffer);
        this.modifpage.getChildren().remove(0);
        this.modifpage.getChildren().add(0, avatar);
    }
    @FXML
    private void retourChat()
    {
        Parent root = Resources.FXML.LOADER.CLAVARCHAT_LOADER.getRoot();
        Scene scene = this.returnButton.getScene();
        ClvcController clvcController = Resources.FXML.LOADER.CLAVARCHAT_LOADER.getController();

        clvcController.onChange();
        scene.setRoot(root);
    }
}