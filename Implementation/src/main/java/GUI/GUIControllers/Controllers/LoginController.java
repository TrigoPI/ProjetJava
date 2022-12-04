package GUI.GUIControllers.Controllers;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.Event;
import javafx.fxml.FXML;
import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.Log.Log;
import javafx.fxml.Initializable;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.scene.Node;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable
{
    private final ClavarChatAPI api;

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

    public void onLoginSuccess()
    {
        this.loginButton.setVisible(true);
        this.loginButton.setManaged(true);

        this.spinnerBar.setVisible(false);
        this.spinnerBar.setManaged(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");

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
        this.loginButton.setVisible(false);
        this.loginButton.setManaged(false);

        this.spinnerBar.setVisible(true);
        this.spinnerBar.setManaged(true);

        String pseudo = this.usernameTextField.getText().trim();
        String id = this.idTextField.getText().trim();
        String password = this.passwordTextField.getText().trim();

        if (pseudo.isEmpty()) this.errorInput(usernameTextField);
        if (id.isEmpty()) this.errorInput(idTextField);
        if (password.isEmpty()) this.errorInput(passwordTextField);

        if (!pseudo.isEmpty() && !id.isEmpty() && !password.isEmpty()) this.api.login(pseudo, id);
    }
}
