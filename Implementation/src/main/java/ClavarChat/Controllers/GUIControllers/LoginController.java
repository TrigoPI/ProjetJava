package ClavarChat.Controllers.GUIControllers;

import javafx.fxml.FXML;
import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.Log.Log;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable
{
    private ClavarChatAPI api;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private MFXButton loginButton;

    @FXML
    private MFXProgressSpinner spinnerBar;

    public LoginController(ClavarChatAPI api)
    {
        this.api = api;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");

        this.spinnerBar.setVisible(false);
        this.spinnerBar.setManaged(false);
    }

    @FXML
    private void handleButtonLogin()
    {
        this.loginButton.setVisible(false);
        this.loginButton.setManaged(false);

        this.spinnerBar.setVisible(true);
        this.spinnerBar.setManaged(true);

        this.api.login("fdfsdf", "zefdsf", () -> {
            this.loginButton.setVisible(true);
            this.loginButton.setManaged(true);

            this.spinnerBar.setVisible(false);
            this.spinnerBar.setManaged(false);

            Log.Error("ERROR LOGIN");
        });
    }
}
