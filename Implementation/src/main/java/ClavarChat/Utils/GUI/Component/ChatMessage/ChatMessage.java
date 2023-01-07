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
    private final Label pseudoLabel;
    private final Avatar avatar;

    private final VBox messageContainer;
    private final String styleClass;
    private final int userId;

    public ChatMessage(int userId, String pseudo, Image img, boolean reverse)
    {
        this.avatar = new Avatar(img, 25, false);
        this.pseudoLabel = new Label(pseudo);
        this.messageContainer = new VBox();
        this.userId = userId;
        this.styleClass = (reverse)?"clvc-american-river":"clvc-shy-moment";

        Pos position = (reverse)?Pos.TOP_LEFT:Pos.TOP_RIGHT;
        VBox avatarContainer = new VBox();
        VBox messageAndNameContainer = new VBox();

        avatarContainer.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        avatarContainer.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        avatarContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        avatarContainer.setAlignment(Pos.BOTTOM_CENTER);

        messageAndNameContainer.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        messageAndNameContainer.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        messageAndNameContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        messageAndNameContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        messageAndNameContainer.setAlignment(position);
        messageAndNameContainer.setSpacing(5);

        pseudoLabel.getStyleClass().add("clvc-pseudo-label");
        pseudoLabel.getStyleClass().add("clvc-font-12px");
        pseudoLabel.getStyleClass().add("clvc-font-bold");

        messageAndNameContainer.getChildren().add(pseudoLabel);
        messageAndNameContainer.getChildren().add(this.messageContainer);

        avatar.displayStatus(false);

        this.messageContainer.setSpacing(3);
        this.messageContainer.setFillWidth(false);
        this.messageContainer.setAlignment(position);

        this.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setAlignment(position);
        this.setSpacing(10);

        avatarContainer.getChildren().add(avatar);

        this.getChildren().add(avatarContainer);
        this.getChildren().add((reverse)?1:0, messageAndNameContainer);
    }

    public int getUserId()
    {
        return this.userId;
    }

    public void addMessage(String message)
    {
        HBox hBox = new HBox();
        Label messageLable = new Label(message);

        messageLable.setMaxSize(644, Region.USE_COMPUTED_SIZE);
        messageLable.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        messageLable.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        messageLable.setWrapText(true);

        hBox.getStyleClass().add(this.styleClass);
        hBox.getStyleClass().add("clvc-chat-bubble");

        hBox.getChildren().add(messageLable);
        this.messageContainer.getChildren().add(hBox);
    }

    public void update(String pseudo, Image image)
    {
        this.pseudoLabel.setText(pseudo);
        this.avatar.changeImage(image);
    }
}
