package GUI.GUIControllers.Controllers;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.fxml.FXML;
import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.Log.Log;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable
{
    private final ClavarChatAPI api;

    @FXML
    private VBox parentContainerLogin;

    @FXML
    private VBox vboxRoot;

    @FXML
    private MFXTextField usernameTextField;

    @FXML
    private MFXTextField idTextField;

    @FXML
    private MFXTextField passwordTextField;

    @FXML
    private MFXButton loginButton;

    @FXML
    private MFXProgressSpinner spinnerBar;

    private final URL url;

    public LoginController(ClavarChatAPI api, URL url)
    {
        this.url = url;
        this.api = api;
    }

    public void onLoginFailed()
    {
        this.loginButton.setVisible(true);
        this.loginButton.setManaged(true);

        this.spinnerBar.setVisible(false);
        this.spinnerBar.setManaged(false);
    }

    public void onLoginSuccess()
    {

//        try
//        {
            this.loginButton.setVisible(true);
            this.loginButton.setManaged(true);

            this.spinnerBar.setVisible(false);
            this.spinnerBar.setManaged(false);

//            Parent root =  FXMLLoader.load(this.url);
//            Scene scene = loginButton.getScene();
//            root.translateXProperty().set(scene.getHeight());
//
//            this.parentContainerLogin.getChildren().add(root);
//
//            Timeline timeline = new Timeline();
//            KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
//            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
//            timeline.getKeyFrames().add(kf);
//            timeline.setOnFinished(t -> {
//                this.parentContainerLogin.getChildren().remove(vboxRoot);
//            });
//            timeline.play();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");

        this.usernameTextField.setText("user");
        this.idTextField.setText("090909");
        this.passwordTextField.setText("qsdfjsdfji");

        this.spinnerBar.setVisible(false);
        this.spinnerBar.setManaged(false);
    }

    private void errorInput(Node node)
    {
        node.getParent().setStyle("-fx-effect: dropshadow(three-pass-box, rgba(232, 65, 24, 0.2), 12, 0.5, 0, 0)");
    }

    @FXML
    private void onInputClick(Event event)
    {

    }

    @FXML
    private void handleButtonLogin()
    {
        String pseudo = this.usernameTextField.getText().trim();
        String id = this.idTextField.getText().trim();
        String password = this.passwordTextField.getText().trim();

        if (pseudo.isEmpty()) this.errorInput(usernameTextField);
        if (id.isEmpty()) this.errorInput(idTextField);
        if (password.isEmpty()) this.errorInput(passwordTextField);

        if (!pseudo.isEmpty() && !id.isEmpty() && !password.isEmpty())
        {
            this.loginButton.setVisible(false);
            this.loginButton.setManaged(false);

            this.spinnerBar.setVisible(true);
            this.spinnerBar.setManaged(true);

            this.api.login(pseudo, id);
        }
    }
}
