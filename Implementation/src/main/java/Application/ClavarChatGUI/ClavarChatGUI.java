package Application.ClavarChatGUI;

import Application.FXMLControllers.Controllers.PasswordController;
import ClavarChat.Utils.Audio.Audio;
import Application.FXMLControllers.Controllers.ClavarChatController;
import Application.FXMLControllers.Controllers.LoginController;
import Application.FXMLControllers.Controllers.SettingsController;
import Application.FXMLControllers.GUIControllers;
import ClavarChat.Resources.Resources;
import javafx.application.Application;
import ClavarChat.ClavarChatAPI;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

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
        PasswordController passwordController     = new PasswordController(this.clavarChatAPI);

        Resources.FXML.LOADER.LOGIN_LOADER.setController(loginController);
        Resources.FXML.LOADER.CLAVARCHAT_LOADER.setController(clavarChatController);
        Resources.FXML.LOADER.SETTINGS_LOADER.setController(settingsController);
        Resources.FXML.LOADER.PASSWORD_LOADER.setController(passwordController);

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