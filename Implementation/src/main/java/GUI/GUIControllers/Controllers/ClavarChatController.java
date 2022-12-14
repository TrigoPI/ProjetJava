package GUI.GUIControllers.Controllers;

import ClavarChat.Utils.GUI.Component.MessageBox.MessageBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.fxml.FXML;
import ClavarChat.Models.Message.Message;
import ClavarChat.Utils.GUI.Component.Avatar.Avatar;
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
import java.util.HashMap;
import java.util.ResourceBundle;

public class ClavarChatController implements Initializable
{
    private final ClavarChatAPI api;
    private final HashMap<String, Discussion> usersGUI;
    private final HashMap<String, MessageBox> usersMessageBox;

    private Discussion selectedUser;

    @FXML
    private MFXScrollPane messagesContainer;

    @FXML
    private HBox userAvatarContainer;

    @FXML
    private VBox otherAvatarContainer;

    @FXML
    private VBox userPreviewContainer;

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
        this.usersMessageBox = new HashMap<>();
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

    public void onTextMessage(String src, String message)
    {
        String dst = this.api.getPseudo();

        if (this.selectedUser != null)
        {
            if (this.selectedUser.getPseudo().equals(src)) Platform.runLater(() -> this.updateChatBox(src, src, message));
        }

        if (!this.api.conversationExist(src)) this.api.createConversation(src);

        this.api.saveMessage(src, src, dst, message);
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
        Discussion discussion = new Discussion(pseudo, this.api.getAvatar(pseudo), pseudo, id);
        discussion.setOnMouseClicked(this::onMouseClick);

        this.createConversation(pseudo);
        this.userPreviewContainer.getChildren().add(discussion);
        this.usersGUI.put(pseudo, discussion);
    }

    private void updateChatBox(String conversationName, String src, String text)
    {
        MessageBox messageBox = this.usersMessageBox.get(conversationName);

        if (src.equals(this.api.getPseudo()))
        {
            messageBox.addMessage(src, this.api.getAvatar(), text, false);
        }
        else
        {
            messageBox.addMessage(src, this.api.getAvatar(src), text, true);
        }

        this.messagesContainer.setVvalue(1.0);
    }

    private void selectUser(Discussion discussion)
    {
        if (!this.chatContainer.isVisible()) this.chatContainer.setVisible(true);
        if (this.selectedUser != null) this.selectedUser.deselect();

        this.selectedUser = discussion;
        String pseudo = this.selectedUser.getPseudo();

        this.messagesContainer.setContent(this.usersMessageBox.get(discussion.getConversationName()));
        this.selectedUser.select();

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

    private void createConversation(String conversationName)
    {
        this.api.createConversation(conversationName);
        this.usersMessageBox.put(conversationName, new MessageBox());
        for (Message message : this.api.getConversation(conversationName)) this.updateChatBox(conversationName, message.srcPseudo, message.text);
    }

    @FXML
    private void onSendMessage()
    {
        String message = this.messageInput.getText().trim();
        String dst = this.selectedUser.getPseudo();
        String src = this.api.getPseudo();

        if (!message.isEmpty())
        {
            this.updateChatBox(dst, src, message);

            this.messageInput.clear();
            this.api.saveMessage(dst, src, dst, message);
            this.api.sendMessage(dst, message);
        }
    }
}