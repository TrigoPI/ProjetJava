package Application.ClavarChatGUI;

import ClavarChat.Utils.Audio.Audio;
import ClavarChat.Utils.Log.Log;
import ClavarChat.Utils.Path.Path;
import GUI.GUIControllers.Controllers.ClavarChatController;
import GUI.GUIControllers.Controllers.LoginController;
import GUI.GUIControllers.Controllers.SettingsController;
import GUI.GUIControllers.GUIControllers;
import javafx.application.Application;
import ClavarChat.ClavarChatAPI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        Audio.init();

        this.clavarChatAPI = new ClavarChatAPI(8080, 7070);

        URL loginFXML = ClavarChatGUI.class.getResource("LoginGUI.fxml");
        URL clavarChatFXML = ClavarChatGUI.class.getResource("ClavarChatGUI.fxml");
        URL settingsFXML = ClavarChatGUI.class.getResource("SettingsPage.fxml");

        SettingsController settingsController = new SettingsController(this.clavarChatAPI);
        FXMLLoader fxmlLoaderSettings = new FXMLLoader(settingsFXML);
        fxmlLoaderSettings.setController(settingsController);

        ClavarChatController clavarChatController = new ClavarChatController(this.clavarChatAPI, fxmlLoaderSettings);
        FXMLLoader fxmlLoaderClavarChat = new FXMLLoader(clavarChatFXML);
        fxmlLoaderClavarChat.setController(clavarChatController);

        LoginController loginController = new LoginController(this.clavarChatAPI, fxmlLoaderClavarChat);
        FXMLLoader fxmlLoaderLogin = new FXMLLoader(loginFXML);
        fxmlLoaderLogin.setController(loginController);


        Scene scene = new Scene(fxmlLoaderLogin.load(), 1080, 720);

        stage.setTitle("ClavaChat!");
        stage.getIcons().add(new Image("file:" + Path.getWorkingPath() + "/src/main/resources/Application/ClavarChatGUI/IMG/Logo.png"));
        stage.setScene(scene);
        stage.show();

        new GUIControllers(this.clavarChatAPI, loginController, clavarChatController);
    }

    @Override
    public void stop()
    {
        this.clavarChatAPI.stop();
        Audio.shutdown();
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