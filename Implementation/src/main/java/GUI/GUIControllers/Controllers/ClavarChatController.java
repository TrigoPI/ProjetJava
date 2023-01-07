package GUI.GUIControllers.Controllers;

import javafx.fxml.FXML;
import ClavarChat.Utils.BytesImage.BytesImage;
import ClavarChat.Models.Message.Message;
import ClavarChat.Models.User.User;
import ClavarChat.Utils.GUI.Component.MessageBox.MessageBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import ClavarChat.Utils.GUI.Component.Avatar.Avatar;
import ClavarChat.Utils.GUI.Component.Discussion.Discussion;
import ClavarChat.ClavarChatAPI;
import ClavarChat.Utils.Log.Log;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ClavarChatController implements Initializable
{
    private final ClavarChatAPI api;
    private final HashMap<String, Discussion> usersGUI;
    private final HashMap<String, MessageBox> messagesBoxGui;
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

    @FXML
    private Button settingButton;

    public ClavarChatController(ClavarChatAPI api)
    {
        this.api = api;
        this.usersGUI = new HashMap<>();
        this.messagesBoxGui = new HashMap<>();
        this.selectedUser = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Log.Print(this.getClass().getName() + " Initialized");

        String pseudo = this.api.getPseudo();
        BytesImage buffer = this.api.getAvatar();
        InputStream in = buffer.toInputStream();
        Image avatar = new Image(in);
        int id = this.api.getId();

        this.userName.setText(pseudo);
        this.userId.setText("#" + id);
        this.chatContainer.setVisible(false);

        this.addAvatar(this.userAvatarContainer, avatar, true, 50, 0);

        this.initDiscussionContainer();
        this.initMessageBox();
    }

    public void onRemoveUser(int userId)
    {
        Platform.runLater(() -> {
            ArrayList<Integer> conversationsId = this.api.getConversationIdWith(userId);
            for (int conversationId : conversationsId)
            {
                String sharedId = this.api.getConversationSharedId(conversationId);
                Discussion discussion = this.usersGUI.get(sharedId);
                discussion.setStatus(false);

                if (this.selectedUser.getSharedId().equals(sharedId))
                {
                    Avatar avatar = (Avatar)otherAvatarContainer.getChildren().get(0);
                    avatar.setStatus(true);
                }
            }
        });
    }

    public void onNewUser(int userId)
    {
        Platform.runLater(() -> {
            ArrayList<Integer> conversationsId = this.api.getConversationIdWith(userId);

            for (int conversationId : conversationsId)
            {
                String sharedId = this.api.getConversationSharedId(conversationId);

                if (!this.usersGUI.containsKey(sharedId))
                {
                    this.createUserDiscussion(userId, conversationId, sharedId);
                    this.messagesBoxGui.put(sharedId, new MessageBox());
                }
                else
                {
                    BytesImage bytesImage = this.api.getAvatar(userId);
                    Image image = new Image(bytesImage.toInputStream());
                    String pseudo = this.api.getPseudo(userId);

                    Discussion discussion = this.usersGUI.get(sharedId);
                    discussion.setStatus(true);
                    discussion.changeAvatar(image);
                    discussion.changePseudo(pseudo);

                    if (this.selectedUser.getSharedId().equals(sharedId))
                    {
                        Avatar avatar = (Avatar)otherAvatarContainer.getChildren().get(0);
                        VBox vBox = (VBox)otherAvatarContainer.getChildren().get(1);
                        Label pseudoLabel = (Label)vBox.getChildren().get(0);
                        avatar.setStatus(true);
                        pseudoLabel.setText(pseudo);
                    }
                }

            }
        });
    }

    private void initDiscussionContainer()
    {
        for (int conversationId : this.api.getConversationsIdInDataBase())
        {
            ArrayList<Integer> usersId = this.api.getUserIdInConversation(conversationId);
            String sharedId = this.api.getConversationSharedId(conversationId);
            this.createUserDiscussion(usersId.get(0), conversationId, sharedId);
        }
    }

    private void initMessageBox()
    {
        for (int conversationId : this.api.getConversationsIdInDataBase())
        {
            String sharedId = this.api.getConversationSharedId(conversationId);
            MessageBox messageBox = new MessageBox();

            for (int messageId : this.api.getMessagesIdInDataBase(conversationId))
            {
                Message message = this.api.getMessageInDataBase(messageId);
                String pseudo = this.api.getPseudoFromDataBase(message.userId);
                BytesImage buffer = this.api.getAvatarFromDataBase(message.userId);
                InputStream in = buffer.toInputStream();
                Image avatar = new Image(in);
                messageBox.addMessage(message.userId, pseudo, avatar, message.text, this.api.getId() != message.userId);
            }

            this.messagesBoxGui.put(sharedId, messageBox);
        }
    }

    private void createUserDiscussion(int userId, int conversationId, String sharedId)
    {
        String pseudo = this.api.getPseudoFromDataBase(userId);
        BytesImage buffer = this.api.getAvatarFromDataBase(userId);
        InputStream in = buffer.toInputStream();
        Image avatar = new Image(in);

        if (this.usersGUI.containsKey(sharedId))
        {
            Discussion discussion = this.usersGUI.get(sharedId);
            discussion.changePseudo(pseudo);
            discussion.changeAvatar(avatar);
            discussion.setStatus(true);
        }
        else
        {
            Discussion discussion = new Discussion(conversationId, sharedId, userId, avatar, pseudo, pseudo + " : blablabla");
            discussion.setStatus(this.api.isConnected(userId));
            discussion.setOnMouseClicked(this::onMouseClick);

            this.userPreviewContainer.getChildren().add(discussion);
            this.usersGUI.put(sharedId, discussion);
        }
    }

    private void addMessage(String sharedId, int userId, String message)
    {
        MessageBox messageBox =  this.messagesBoxGui.get(sharedId);
        String pseudo = this.api.getPseudoFromDataBase(userId);
        BytesImage buffer = this.api.getAvatarFromDataBase(userId);
        InputStream in = buffer.toInputStream();
        Image avatar = new Image(in);
        messageBox.addMessage(userId, pseudo, avatar, message, this.api.getId() != userId);
    }

    public void onTextMessage(String sharedId, int userId, String message)
    {
        Platform.runLater(() -> this.updateChatBox(sharedId, userId, message));
    }

    private void addAvatar(Pane container, Image img, boolean connected, double radius, int index)
    {
        Platform.runLater(() -> {
            Avatar avatar = new Avatar(img, radius, connected);
            container.getChildren().add(index, avatar);
        });
    }

    private void updateChatBox(String sharedId, int userId, String text)
    {
        String pseudo = this.api.getPseudo(userId);
        BytesImage avatar = this.api.getAvatar(userId);
        Image image = new Image(avatar.toInputStream());
        MessageBox messageBox = this.messagesBoxGui.get(sharedId);
        messageBox.addMessage(userId, pseudo, image, text, !(this.api.getId() == userId));
        this.messagesContainer.setVvalue(1.0);
    }

    private void selectUser(Discussion discussion)
    {
        if (!this.chatContainer.isVisible()) this.chatContainer.setVisible(true);
        if (this.selectedUser != null) this.selectedUser.deselect();

        this.selectedUser = discussion;
        this.messagesContainer.setContent(this.messagesBoxGui.get(discussion.getSharedId()));
        this.selectedUser.select();

        this.updateChatContainer(discussion.getUserId());
    }

    private void updateChatContainer(int userId)
    {
        User user = this.api.getUserInDataBase(userId);
        BytesImage buffer = this.api.getAvatarFromDataBase(userId);

        VBox vBox = new VBox();
        Label pseudoLabel = new Label(user.pseudo);
        Label idLabel = new Label("#" + userId);
        InputStream in = buffer.toInputStream();
        Image avatar = new Image(in);

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
        this.addAvatar(this.otherAvatarContainer, avatar, this.api.isConnected(userId), 50, 0);
    }

    private void onMouseClick(MouseEvent event)
    {
        this.selectUser((Discussion)event.getSource());
    }

    @FXML
    private void onSendMessage()
    {
        int userId  = this.api.getId();
        int otherId = this.selectedUser.getUserId();
        int conversationId = this.selectedUser.getConversationId();
        String sharedId = this.selectedUser.getSharedId();
        String message = this.messageInput.getText().trim();

        if (!message.isEmpty())
        {
            this.addMessage(sharedId, userId, message);
            this.api.sendMessage(userId, otherId, conversationId, message);
            this.messageInput.clear();
        }
    }

    @FXML
    private void onSettings()
    {
        System.out.println("OOOOOOOOOOOOOOOOK");
    }
}