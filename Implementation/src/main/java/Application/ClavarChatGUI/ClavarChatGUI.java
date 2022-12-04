package Application.ClavarChatGUI;

import GUI.GUIControllers.Controllers.LoginController;
import GUI.GUIControllers.GUIControllers;
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
        URL fxmlURL = ClavarChatGUI.class.getResource("LoginGUI.fxml");
        URL fxmlURL2 = ClavarChatGUI.class.getResource("ClavarChatGUI.fxml");

        ClavarChatAPI clavarChatAPI = new ClavarChatAPI(8080, 7070);
        LoginController loginController = new LoginController(clavarChatAPI, fxmlURL2);
        GUIControllers guiControllers = new GUIControllers(clavarChatAPI, loginController);

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

    public static void runApplication()
    {
        ClavarChatGUI clavarChatGUI = new ClavarChatGUI();
        clavarChatGUI.run();
    }
}
