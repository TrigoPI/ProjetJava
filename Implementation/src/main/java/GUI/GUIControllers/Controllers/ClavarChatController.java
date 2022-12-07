package GUI.GUIControllers.Controllers;

import ClavarChat.ClavarChatAPI;
import ClavarChat.Models.Users.User;
import ClavarChat.Utils.Log.Log;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClavarChatController implements Initializable
{
    @FXML
    private Label userName;

    @FXML
    private Label userId;

    @FXML
    private VBox userPreviewContainer;

    ClavarChatAPI api;

    public ClavarChatController(ClavarChatAPI api)
    {
        this.api = api;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");

        String pseudo = this.api.getPseudo();
        String id = this.api.getId();

        this.userName.setText(pseudo);
        this.userId.setText("#" + id);

        this.addDiscoveredUser();
    }

    public void onNewUser(String pseudo, String id)
    {
        Platform.runLater(() -> {
            this.createUserDescription(pseudo, id);
        });
    }

    private void addDiscoveredUser()
    {
        for (User user : this.api.getUsers()) this.createUserDescription(user.pseudo, user.id);
    }

    private void createUserDescription(String pseudo, String id)
    {
        HBox container = new HBox();
        Circle avatar = new Circle();
        VBox desContainer = new VBox();
        Label userName = new Label(pseudo);
        Label userId = new Label(id);

        container.setSpacing(15);
        container.setAlignment(Pos.CENTER_LEFT);

        desContainer.setAlignment(Pos.CENTER_LEFT);
        desContainer.setPrefSize(120, 80);
        desContainer.setSpacing(20);

        avatar.setFill(Color.BLUE);
        avatar.setRadius(25);

        desContainer.getChildren().add(userName);
        desContainer.getChildren().add(userId);

        container.getChildren().add(avatar);
        container.getChildren().add(desContainer);

        container.getStyleClass().add("clvc-american-river");
        container.getStyleClass().add("clvc-user-preview-container");

        this.userPreviewContainer.getChildren().add(container);
    }
}
