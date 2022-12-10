package GUI.GUIControllers.Controllers;

import ClavarChat.Models.Message.Message;
import javafx.fxml.FXML;
import ClavarChat.ClavarChatAPI;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Animation.FadeColor.FadeColor;
import ClavarChat.Utils.Log.Log;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ClavarChatController implements Initializable
{
    private final ClavarChatAPI api;
    private final HashMap<String, HBox> usersGUI;

    private final Color draculaOrchid;
    private final Color americanRiver;

    private HBox selectedUser;

    @FXML
    private HBox userAvatarContainer;

    @FXML
    private HBox otherAvatarContainer;

    @FXML
    private VBox userPreviewContainer;

    @FXML
    private VBox messagesContainer;

    @FXML
    private VBox chatContainer;

    @FXML
    private Label userName;

    @FXML
    private Label userId;

    @FXML
    private Label otherUserName;

    @FXML
    private Label otherUserId;

    @FXML
    private ImageView userAvatarImg;

    @FXML
    private ImageView otherAvatarImg;

    @FXML
    private MFXTextField messageInput;

    public ClavarChatController(ClavarChatAPI api)
    {
        this.api = api;
        this.usersGUI = new HashMap<>();
        this.selectedUser = null;

        this.draculaOrchid = new Color(45.0 / 255.0, 52.0 / 255.0, 54.0 / 255.0,1.0);
        this.americanRiver = new Color(99.0 / 255.0, 110.0 / 255.0, 114.0 / 255.0, 1.0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");

        String pseudo = this.api.getPseudo();
        String id = this.api.getId();

        this.userAvatarImg.setImage(this.api.getAvatar());
        this.userName.setText(pseudo);
        this.userId.setText("#" + id);

        this.addDiscoveredUser();

        this.clip(this.userAvatarImg, 50);
    }

    public void onRemoveUser(String pseudo)
    {
        HBox container = this.usersGUI.get(pseudo);
        Platform.runLater(() -> this.userPreviewContainer.getChildren().remove(container));
    }

    public void onNewUser(String pseudo)
    {
        Platform.runLater(() -> {
            this.createUserDescription(pseudo, this.api.getId(pseudo));

            if (!this.chatContainer.isVisible())
            {
                this.chatContainer.setVisible(true);
                this.selectUser(this.usersGUI.get(pseudo));
            }
        });
    }

    public void onTextMessage(String pseudo, String message)
    {
        if (this.selectedUser != null)
        {
            String selectedPseudo = this.getSelectedUserPseudo();
            if (selectedPseudo.equals(pseudo)) Platform.runLater(() -> this.createMessage(message, true));
        }

        this.api.saveMessage(pseudo, pseudo, message);
    }

    private String getSelectedUserPseudo()
    {
        if (this.selectedUser == null) return null;

        VBox vBox = (VBox)this.selectedUser.getChildren().get(1);
        Label label = (Label)vBox.getChildren().get(0);

        return label.getText();
    }

    private void clip(ImageView img, double radius)
    {
        Platform.runLater(() -> {
            img.setClip(null);

            Circle circle = new Circle();
            circle.setRadius(radius);

            double w = img.getBoundsInParent().getWidth();
            double h = img.getBoundsInParent().getHeight();

            circle.setLayoutX(w / 2.0);
            circle.setLayoutY(h / 2.0);

            img.setClip(circle);
        });
    }

    private void addDiscoveredUser()
    {
        for (User user : this.api.getUsers()) this.createUserDescription(user.pseudo, user.id);

        if (!this.userPreviewContainer.getChildren().isEmpty())
        {
            this.selectUser((HBox)this.userPreviewContainer.getChildren().get(0));
        }
        else
        {
            this.chatContainer.setVisible(false);
        }
    }

    private void createUserDescription(String pseudo, String id)
    {
        HBox container = new HBox();
        HBox avatarContainer = new HBox();
        ImageView avatar = new ImageView();
        VBox desContainer = new VBox();
        Label userName = new Label(pseudo);
        Label text = new Label(id);

        container.setOnMouseEntered(this::onMouseEntered);
        container.setOnMouseExited(this::onMouseExited);
        container.setOnMouseClicked(this::onMouseClick);
        container.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        container.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        container.setPrefSize(Region.USE_COMPUTED_SIZE, 100);
        container.setAlignment(Pos.CENTER_LEFT);

        avatarContainer.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        avatarContainer.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        avatarContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        desContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        desContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        desContainer.setAlignment(Pos.CENTER_LEFT);
        desContainer.setPrefSize(120, 80);
        desContainer.setSpacing(20);

        avatar.setFitWidth(100);
        avatar.setFitHeight(0);
        avatar.setPreserveRatio(true);
        avatar.setImage(this.api.getAvatar(pseudo));

        desContainer.getChildren().add(userName);
        desContainer.getChildren().add(text);

        avatarContainer.getChildren().add(avatar);

        container.getChildren().add(avatar);
        container.getChildren().add(desContainer);

        userName.getStyleClass().add("clvc-font-bold");
        text.getStyleClass().add("clvc-font-12px");

        container.getStyleClass().add("clvc-user-preview-container");
        container.getStyleClass().add("clvc-dracula-orchid");
        container.getStyleClass().add("clvc-cursor-hand");

        this.clip(avatar, 30);
        this.usersGUI.put(pseudo, container);
        this.userPreviewContainer.getChildren().add(container);
    }

    private void createMessage(String message, boolean other)
    {
        Pos pos = (other)?Pos.CENTER_LEFT:Pos.CENTER_RIGHT;
        String color = (other)?"clvc-american-river":"clvc-shy-moment";

        HBox container = new HBox();
        HBox messageContainer = new HBox();
        HBox avatarContainer = new HBox();
        Label messageLabel = new Label();
        ImageView avatar = new ImageView();

        container.setAlignment(pos);
        container.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        container.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        container.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        container.setFillHeight(false);
        container.setSpacing(10);

        messageLabel.setText(message);

        messageContainer.setAlignment(pos);
        messageContainer.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        messageContainer.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        messageContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        messageContainer.getStyleClass().add(color);
        messageContainer.getStyleClass().add("clvc-chat-bubble");

        avatarContainer.setAlignment(pos);
        avatarContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
        avatarContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
        avatarContainer.setPrefSize(80, Region.USE_COMPUTED_SIZE);

        avatar.setFitWidth(100);
        avatar.setFitHeight(0);
        avatar.setPreserveRatio(true);
        avatar.setImage(this.api.getAvatar());

        messageContainer.getChildren().add(messageLabel);
        avatarContainer.getChildren().add(avatar);

        if (other)
        {
            container.getChildren().add(avatarContainer);
            container.getChildren().add(messageContainer);
        }
        else
        {
            container.getChildren().add(messageContainer);
            container.getChildren().add(avatarContainer);
        }

        this.clip(avatar, 30);
        this.messagesContainer.getChildren().add(container);
    }

    private void selectUser(HBox hBox)
    {
        if (this.selectedUser != null)
        {
            this.selectedUser.getStyleClass().remove("clvc-american-river");
            this.selectedUser.getStyleClass().add("clvc-dracula-orchid");
            this.selectedUser.setBackground(new Background(new BackgroundFill(this.draculaOrchid, CornerRadii.EMPTY, Insets.EMPTY)));
        }

        this.selectedUser = hBox;

        this.selectedUser.getStyleClass().remove("clvc-dracula-orchid");
        this.selectedUser.getStyleClass().add("clvc-american-river");

        this.updateChatContainer(this.getSelectedUserPseudo());
        this.clip(this.otherAvatarImg, 50);
    }

    private void updateChatContainer(String pseudo)
    {
        Image avatar = this.api.getAvatar(pseudo);

        this.otherAvatarImg.setImage(avatar);
        this.otherUserName.setText(pseudo);
        this.otherUserId.setText("#" + this.api.getId(pseudo));
    }

    private void onMouseExited(MouseEvent event)
    {
        HBox hBox = (HBox)event.getSource();

        if (this.selectedUser != hBox)
        {
            Color startColor = (Color)hBox.getBackground().getFills().get(0).getFill();
            FadeColor fadeColor = new FadeColor(startColor, this.draculaOrchid, hBox, 200);
            fadeColor.playFromStart();
        }
    }

    private void onMouseEntered(MouseEvent event)
    {
        HBox hBox = (HBox)event.getSource();

        if (this.selectedUser != hBox)
        {
            Color startColor = (Color)hBox.getBackground().getFills().get(0).getFill();
            FadeColor fadeColor = new FadeColor(startColor, this.americanRiver, hBox, 200);
            fadeColor.playFromStart();
        }
    }

    private void onMouseClick(MouseEvent event)
    {
        if (this.selectedUser != null) this.messagesContainer.getChildren().clear();
        this.selectUser((HBox)event.getSource());

        String otherPseudo = this.getSelectedUserPseudo();
        String userPseudo  = this.api.getPseudo();

        if (this.api.conversationExist(otherPseudo))
        {
            ArrayList<Message> conversation = this.api.getConversation(otherPseudo);
            this.createConversation(otherPseudo, userPseudo);
        }
        else
        {
            this.api.createConversation(otherPseudo);
        }
    }

    private void createConversation(String otherPseudo, String userPseudo)
    {
        ArrayList<Message> conversation = this.api.getConversation(otherPseudo);

        for (Message message : conversation)
        {
            boolean me = message.pseudo.equals(userPseudo);
            this.createMessage(message.text, me);
        }
    }

    @FXML
    private void onSendMessage()
    {
        String message = this.messageInput.getText().trim();
        String pseudo = this.getSelectedUserPseudo();
        String from = this.api.getPseudo();

        if (!message.isEmpty())
        {
            this.createMessage(message, false);

            this.messageInput.clear();
            this.api.saveMessage(pseudo, from, message);
            this.api.sendMessage(pseudo, message);
        }
    }
}