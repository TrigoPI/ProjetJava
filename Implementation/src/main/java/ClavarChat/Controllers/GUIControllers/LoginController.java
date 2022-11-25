package ClavarChat.Controllers.GUIControllers;

import javafx.fxml.FXML;
import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.Log.Log;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable
{
    private ClavarChatAPI api;

    @FXML
    private TextField username;

    @FXML
    private TextField id;

    @FXML
    private TextField password;

    @FXML
    private Button login;

    public LoginController(ClavarChatAPI api)
    {
        this.api = api;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");
    }

    @FXML
    private void handleLogin()
    {
        Log.Error("Username : " + username.getText() + " / id : " + id.getText() + " / password : " + password.getText());
    }
}
