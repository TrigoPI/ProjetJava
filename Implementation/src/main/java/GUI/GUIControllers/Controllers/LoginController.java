package GUI.GUIControllers.Controllers;

import javafx.fxml.FXML;
import ClavarChat.Utils.GUI.Animation.FadeColor.FadeColor;
import ClavarChat.Utils.Path.Path;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.Event;
import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.Log.Log;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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

    private final FXMLLoader clavarChat;

    public LoginController(ClavarChatAPI api, FXMLLoader clavarChat)
    {
        this.api = api;
        this.clavarChat = clavarChat;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");

        this.usernameTextField.setText("Alexis");
        this.idTextField.setText("6969");
        this.passwordTextField.setText("qsdfjsdfji");

        this.spinnerBar.setVisible(false);
        this.spinnerBar.setManaged(false);

        this.loginButton.setOnMouseEntered(this::onMouseEntered);
        this.loginButton.setOnMouseExited(this::onMouseExited);
    }

    public void onLoginFailed()
    {
        this.loginButton.setVisible(true);
        this.loginButton.setManaged(true);

        this.spinnerBar.setVisible(false);
        this.spinnerBar.setManaged(false);

        this.errorInput(this.usernameTextField);
    }

    public void onLoginSuccess()
    {
        try
        {
            this.spinnerBar.setVisible(false);
            this.spinnerBar.setManaged(false);

            Parent root  = this.clavarChat.load();
            Scene scene = loginButton.getScene();

            scene.setRoot(root);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void errorInput(Node node)
    {
        node.getParent().getStyleClass().add("clvc-input-field-error");
    }

    private void onMouseEntered(MouseEvent event)
    {
        Color startColor = (Color)this.loginButton.getBackground().getFills().get(0).getFill();
        Color endColor = new Color(56.0 / 255.0, 1.0, 172.0 / 255.0, 1.0);
        FadeColor fadeColor = new FadeColor(startColor, endColor, this.loginButton, 200);
        fadeColor.playFromStart();
    }

    private void onMouseExited(MouseEvent event)
    {
        Color startColor = (Color)this.loginButton.getBackground().getFills().get(0).getFill();
        Color endColor = new Color(123.0 / 255.0, 237.0 / 255.0, 159.0 / 255.0, 1.0);
        FadeColor fadeColor = new FadeColor(startColor, endColor, this.loginButton, 200);
        fadeColor.playFromStart();
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

            this.api.login(pseudo, Integer.parseInt(id), Path.getWorkingPath() + "\\src\\main\\resources\\Application\\ClavarChatGUI\\IMG\\avatar.jpg");
        }
    }
}