package GUI.GUIControllers.Controllers;

import javafx.fxml.FXML;
import ClavarChat.Models.Message.Message;
import ClavarChat.Utils.GUI.Component.Avatar.Avatar;
import ClavarChat.Utils.GUI.Component.ChatMessage.ChatMessage;
import ClavarChat.Utils.GUI.Component.Discussion.Discussion;
import ClavarChat.ClavarChatAPI;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.Log.Log;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ClavarChatController implements Initializable
{
    private final ClavarChatAPI api;
    private final HashMap<String, Discussion> usersGUI;

    private Discussion selectedUser;

    @FXML
    private HBox userAvatarContainer;

    @FXML
    private VBox otherAvatarContainer;

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
    private MFXTextField messageInput;

    public ClavarChatController(ClavarChatAPI api)
    {
        this.api = api;
        this.usersGUI = new HashMap<>();
        this.selectedUser = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");

        String pseudo = this.api.getPseudo();
        String id = this.api.getId();

        this.userName.setText(pseudo);
        this.userId.setText("#" + id);

        this.addAvatar(this.userAvatarContainer, this.api.getAvatar(), 50, 0);
        this.addDiscoveredUser();
    }

    public void onRemoveUser(String pseudo)
    {
        Platform.runLater(() -> {
            Discussion container = this.usersGUI.get(pseudo);

            if (this.selectedUser != null)
            {
                if (this.selectedUser.getPseudo().equals(pseudo)) this.chatContainer.setVisible(false);
            }

            this.userPreviewContainer.getChildren().remove(container);
        });
    }

    public void onNewUser(String pseudo)
    {
        Platform.runLater(() -> {
            this.createUserDescription(pseudo, this.api.getId(pseudo));
            this.selectUser(this.usersGUI.get(pseudo));
        });
    }

    public void onTextMessage(String pseudo, String message)
    {
        if (this.selectedUser != null)
        {
//            if (this.selectedUser.getPseudo().equals(pseudo)) Platform.runLater(() -> this.createMessage(pseudo, message));
        }

        if (!this.api.conversationExist(pseudo)) this.api.createConversation(pseudo);

        this.api.saveMessage(pseudo, pseudo, message);
    }

    private void addAvatar(Pane container, Image img, double radius, int index)
    {
        Platform.runLater(() -> {
            Avatar avatar = new Avatar(img, radius);
            container.getChildren().add(index, avatar);
        });
    }

    private void addDiscoveredUser()
    {
        for (User user : this.api.getUsers()) this.createUserDescription(user.pseudo, user.id);

        if (!this.userPreviewContainer.getChildren().isEmpty())
        {
            this.selectUser((Discussion)this.userPreviewContainer.getChildren().get(0));
        }
        else
        {
            this.chatContainer.setVisible(false);
        }
    }

    private void createUserDescription(String pseudo, String id)
    {
        Discussion discussion = new Discussion(this.api.getAvatar(pseudo), pseudo, id);
        discussion.setOnMouseClicked(this::onMouseClick);

        this.usersGUI.put(pseudo, discussion);
        this.userPreviewContainer.getChildren().add(discussion);
    }

    private void createChatBublle(String src, String dst, String text)
    {
        Message lastMessage = this.api.getLastMessageWith(dst);

        if (lastMessage != null)
        {
            int index = this.chatContainer.getChildren().size() - 1;
            ChatMessage lastChatMessage = (ChatMessage) this.chatContainer.getChildren().get(index);
        }
    }

    private void selectUser(Discussion discussion)
    {
        if (!this.chatContainer.isVisible()) this.chatContainer.setVisible(true);
        if (this.selectedUser != null) this.selectedUser.deselect();

        this.selectedUser = discussion;
        String pseudo = this.selectedUser.getPseudo();

        this.messagesContainer.getChildren().clear();
        this.selectedUser.select();

        if (!this.api.conversationExist(pseudo)) this.api.createConversation(pseudo);

        this.createConversation(pseudo);
        this.updateChatContainer(pseudo);
    }

    private void updateChatContainer(String pseudo)
    {
        VBox vBox = new VBox();
        Label pseudoLabel = new Label(pseudo);
        Label idLabel = new Label("#" + this.api.getId(pseudo));

        pseudoLabel.getStyleClass().add("clvc-font-24px");
        pseudoLabel.getStyleClass().add("clvc-font-bold");

        idLabel.getStyleClass().add("clvc-font-12px");

        vBox.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        vBox.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        vBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setSpacing(5);

        vBox.getChildren().add(pseudoLabel);
        vBox.getChildren().add(idLabel);

        this.otherAvatarContainer.getChildren().clear();
        this.otherAvatarContainer.getChildren().add(vBox);
        this.addAvatar(this.otherAvatarContainer, this.api.getAvatar(pseudo), 50, 0);
    }

    private void onMouseClick(MouseEvent event)
    {
        this.selectUser((Discussion)event.getSource());
    }

    private void createConversation(String otherPseudo)
    {
        ArrayList<Message> conversation = this.api.getConversation(otherPseudo);
//        for (Message message : conversation) this.createMessage(message.pseudo, this.api.getPseudo(), message.text);
    }

    @FXML
    private void onSendMessage()
    {
        String message = this.messageInput.getText().trim();
        String pseudo = this.selectedUser.getPseudo();
        String from = this.api.getPseudo();

        if (!message.isEmpty())
        {
//            this.createMessage(this.api.getPseudo(), pseudo, message);

            this.messageInput.clear();
            this.api.saveMessage(pseudo, from, message);
            this.api.sendMessage(pseudo, message);
        }
    }
}