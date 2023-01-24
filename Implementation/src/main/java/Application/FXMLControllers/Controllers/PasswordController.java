package Application.FXMLControllers.Controllers;

import ClavarChat.Utils.Path.Path;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.json.simple.*;
import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.BytesImage.BytesImage;
import Application.JavaFXComponents.Component.Avatar.Avatar;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Resources.Resources;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
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
public class PasswordController extends ClvcController {

    @FXML
    private MFXPasswordField oldPassword;
    @FXML
    private MFXPasswordField newPassword;
    @FXML
    private MFXPasswordField confPassword;
    @FXML
    private Button returnButton;

    public PasswordController(ClavarChatAPI api) {super(api);}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Log.Print(this.getClass().getName() + " Initialized");
    }

    @Override
    public void onChange() {
    }
    public void onConfFailed()
    {
        Color c = Color.web("#FE003E");
        this.confPassword.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
        this.newPassword.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public void onPasswordFailed()
    {
        Color c = Color.web("#FE003E");
        this.oldPassword.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    private void returnHandler() throws IOException {
        Parent root = Resources.FXML.LOADER.SETTINGS_LOADER.load();
        Scene scene = this.returnButton.getScene();
        ClvcController clvcController = Resources.FXML.LOADER.CLAVARCHAT_LOADER.getController();
        clvcController.onChange();
        scene.setRoot(root);
    }
    @FXML
    private void validation()
    {
        JSONObject jsonObject = Path.parseJSON(Resources.CONFIG.CONF_FILE);
        String password = (String)jsonObject.get("password");
        String actualPassword = this.oldPassword.getText().trim();
        String newPassword = this.newPassword.getText().trim();
        String confPassword = this.confPassword.getText().trim();
        if (actualPassword==password)
        {
            if(newPassword==confPassword)
            {
                jsonObject.put("password",newPassword);
            }
            else{this.onConfFailed();}
        }
        else {this.onPasswordFailed();}
    }


}


