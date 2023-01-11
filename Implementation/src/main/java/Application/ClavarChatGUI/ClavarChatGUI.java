package Application.ClavarChatGUI;

import ClavarChat.Utils.Audio.Audio;
import GUI.GUIControllers.Controllers.ClavarChatController;
import GUI.GUIControllers.Controllers.LoginController;
import GUI.GUIControllers.Controllers.SettingsController;
import GUI.GUIControllers.GUIControllers;
import Resources.Resources;
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
        Audio.init();

        this.clavarChatAPI = new ClavarChatAPI(8080, 7070);

        LoginController loginController           = new LoginController(this.clavarChatAPI);
        ClavarChatController clavarChatController = new ClavarChatController(this.clavarChatAPI);
        SettingsController settingsController     = new SettingsController(this.clavarChatAPI);

        Resources.FXML.LOADER.LOGIN_LOADER.setController(loginController);
        Resources.FXML.LOADER.CLAVARCHAT_LOADER.setController(clavarChatController);
        Resources.FXML.LOADER.SETTINGS_LOADER.setController(settingsController);

        Scene scene = new Scene(Resources.FXML.LOADER.LOGIN_LOADER.load(), 1080, 720);

        stage.setTitle("ClavaChat!");
        stage.getIcons().add(new Image(Resources.IMG.LOGO));
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