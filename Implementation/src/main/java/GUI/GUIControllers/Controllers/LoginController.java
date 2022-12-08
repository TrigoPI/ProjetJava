package GUI.GUIControllers.Controllers;

import io.github.palexdev.materialfx.controls.MFXTextField;
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
        this.clavarChat = clavarChat;
        this.api = api;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");

        this.usernameTextField.setText("Bastien");
        this.idTextField.setText("2222");
        this.passwordTextField.setText("qsdfjsdfji");

        this.spinnerBar.setVisible(false);
        this.spinnerBar.setManaged(false);
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
