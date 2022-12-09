package Application.ClavarChatGUI;

import ClavarChat.Utils.Log.Log;
import GUI.GUIControllers.Controllers.ClavarChatController;
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
    ClavarChatAPI clavarChatAPI;

    @Override
    public void start(Stage stage) throws IOException
    {
        Log.clearLogFile();

        this.clavarChatAPI = new ClavarChatAPI(8080, 7070);

        URL loginFXML = ClavarChatGUI.class.getResource("LoginGUI.fxml");
        URL clavarChatFXML = ClavarChatGUI.class.getResource("ClavarChatGUI.fxml");

        ClavarChatController clavarChatController = new ClavarChatController(this.clavarChatAPI);
        FXMLLoader fxmlLoaderClavarChat = new FXMLLoader(clavarChatFXML);
        fxmlLoaderClavarChat.setController(clavarChatController);

        LoginController loginController = new LoginController(this.clavarChatAPI, fxmlLoaderClavarChat);
        FXMLLoader fxmlLoaderLogin = new FXMLLoader(loginFXML);
        fxmlLoaderLogin.setController(loginController);

        Scene scene = new Scene(fxmlLoaderLogin.load(), 1080, 720);
//        Scene scene = new Scene(fxmlLoaderClavarChat.load(), 1080, 720);

        stage.setTitle("ClavaChat!");
        stage.setScene(scene);
        stage.show();

        new GUIControllers(this.clavarChatAPI, loginController, clavarChatController);
    }

    @Override
    public void stop() throws Exception
    {
        this.clavarChatAPI.logout();
        this.clavarChatAPI.closeServers();
        this.clavarChatAPI.closeAllClient();
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