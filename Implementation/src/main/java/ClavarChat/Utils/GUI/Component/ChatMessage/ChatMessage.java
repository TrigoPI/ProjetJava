package ClavarChat.Utils.GUI.Component.ChatMessage;

import ClavarChat.Utils.GUI.Component.Avatar.Avatar;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ChatMessage extends HBox
{
    private VBox messageContainer;
    private String from;

    public ChatMessage(Image img, String from)
    {
        this.messageContainer = new VBox();
        this.from = from;

        VBox avatarContainer = new VBox();
        Avatar avatar = new Avatar(img, 25);

        avatarContainer.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        avatarContainer.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        avatarContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        avatarContainer.setAlignment(Pos.BOTTOM_CENTER);

        this.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_RIGHT);

        avatarContainer.getChildren().add(avatar);

        this.getChildren().add(this.messageContainer);
        this.getChildren().add(avatarContainer);
    }

    public boolean isFrom(String pseudo)
    {
        return this.from.equals(pseudo);
    }

    public String getFrom()
    {
        return this.from;
    }

    public void addMessage(String message)
    {
        HBox hBox = new HBox();
        Label messageLable = new Label(message);

        hBox.getStyleClass().add("clvc-shy-moment");
        hBox.getStyleClass().add("clvc-chat-bubble");

        hBox.getChildren().add(messageLable);
        this.messageContainer.getChildren().add(hBox);
    }
}
