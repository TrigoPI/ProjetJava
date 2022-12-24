package GUI.GUIControllers.Controllers;

import javafx.fxml.FXML;
import ClavarChat.Models.BytesImage.BytesImage;
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
    private final HashMap<Integer, Discussion> usersGUI;
    private final HashMap<Integer, MessageBox> messagesBoxGui;
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

        this.addAvatar(this.userAvatarContainer, avatar, true, 50, 0);
        this.initDiscussionContainer();
    }

    public void onRemoveUser(String pseudo)
    {
//        Platform.runLater(() -> {
//            Discussion container = this.usersGUI.get(pseudo);
//
//            if (this.selectedUser != null)
//            {
//                if (this.selectedUser.getPseudo().equals(pseudo)) this.chatContainer.setVisible(false);
//            }
//
//            this.userPreviewContainer.getChildren().remove(container);
//        });
    }

    public void onNewUser(String pseudo)
    {
        Platform.runLater(() -> {
//            this.createUserDiscussion(pseudo, this.api.getId(pseudo));
//            this.selectUser(this.usersGUI.get(pseudo));
        });
    }

    private void initDiscussionContainer()
    {
        for (int conversationId : this.api.getConversationsIdInDataBase())
        {
            ArrayList<Integer> usersId = this.api.getUserIdInConversation(conversationId);
            this.messagesBoxGui.put(conversationId, new MessageBox());
            this.createUserDiscussion(usersId.get(0), conversationId);
            this.initMessageBox(conversationId);
        }
    }

    private void initMessageBox(int conversationId)
    {
        MessageBox messageBox = this.messagesBoxGui.get(conversationId);

        for (int id : this.api.getMessagesIdInDataBase(conversationId))
        {
            Message message = this.api.getMessageInDataBase(id);
            String pseudo = this.api.getPseudoFromDataBase(message.userId);
            BytesImage buffer = this.api.getAvatarFromDataBase(message.userId);
            InputStream in = buffer.toInputStream();
            Image avatar = new Image(in);
            messageBox.addMessage(message.userId, pseudo, avatar, message.text, this.api.getId() != message.userId);
        }

    }

    private void createUserDiscussion(int userId, int conversationId)
    {
        String pseudo = this.api.getPseudoFromDataBase(userId);
        BytesImage buffer = this.api.getAvatarFromDataBase(userId);
        InputStream in = buffer.toInputStream();
        Image avatar = new Image(in);

        Discussion discussion = new Discussion(conversationId, userId, this.api.isConnected(userId), avatar, pseudo, pseudo + " : blablabla");
        discussion.setOnMouseClicked(this::onMouseClick);

        this.userPreviewContainer.getChildren().add(discussion);
        this.usersGUI.put(conversationId, discussion);
    }

    private void addMessage(int conversationId, int userId, String message)
    {
        MessageBox messageBox =  this.messagesBoxGui.get(conversationId);
        String pseudo = this.api.getPseudoFromDataBase(userId);
        BytesImage buffer = this.api.getAvatarFromDataBase(userId);
        InputStream in = buffer.toInputStream();
        Image avatar = new Image(in);
        messageBox.addMessage(userId, pseudo, avatar, message, this.api.getId() != userId);
        this.api.saveMessage(conversationId, userId, message);
    }

    public void onTextMessage(String src, String message)
    {
        String dst = this.api.getUser().pseudo;

        if (this.selectedUser != null)
        {
//            if (this.selectedUser.getPseudo().equals(src)) Platform.runLater(() -> this.updateChatBox(src, src, message));
        }

//        if (!this.api.conversationExist(src)) this.api.createConversation(src);

//        this.api.saveMessage(src, src, dst, message);
    }

    private void addAvatar(Pane container, Image img, boolean connected, double radius, int index)
    {
        Platform.runLater(() -> {
            Avatar avatar = new Avatar(img, radius, connected);
            container.getChildren().add(index, avatar);
        });
    }

    private void updateChatBox(int conversationId, String src, String text)
    {
//        messageBox.addMessage(src, this.api.getAvatar(src), text, !this.api.getPseudo().equals(src));
        this.messagesContainer.setVvalue(1.0);
    }

    private void selectUser(Discussion discussion)
    {
        if (!this.chatContainer.isVisible()) this.chatContainer.setVisible(true);
        if (this.selectedUser != null) this.selectedUser.deselect();

        this.selectedUser = discussion;
        this.messagesContainer.setContent(this.messagesBoxGui.get(discussion.getConversationId()));
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

    private void createConversation(String conversationName)
    {
        this.api.createConversation();
//        this.usersMessageBox.put(conversationName, new MessageBox());
//        for (Message message : this.api.getConversation(conversationName)) this.updateChatBox(conversationName, message.srcPseudo, message.text);
    }

    @FXML
    private void onSendMessage()
    {
        int userId = this.api.getId();
        int conversationId = this.selectedUser.getConversationId();
        String message = this.messageInput.getText().trim();

        System.out.println(conversationId);

        if (!message.isEmpty())
        {
            this.addMessage(conversationId, userId, message);
            this.messageInput.clear();
        }
    }
}