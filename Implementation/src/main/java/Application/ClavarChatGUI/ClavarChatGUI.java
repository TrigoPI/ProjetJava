package Application.ClavarChatGUI;

import ClavarChat.Controllers.GUIControllers.LoginController;
import javafx.application.Application;
import ClavarChat.ClavarChatAPI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ClavarChatGUI extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        ClavarChatAPI clavarChatAPI = new ClavarChatAPI(4000, 5000);

        LoginController loginController = new LoginController(clavarChatAPI);

        URL fxmlURL = ClavarChatGUI.class.getResource("LoginGUI.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
        fxmlLoader.setController(loginController);

        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        stage.setTitle("ClavaChat!");
        stage.setScene(scene);
        stage.show();
    }

    public void run()
    {
        launch();
    }
}
