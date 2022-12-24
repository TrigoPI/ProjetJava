package ClavarChat.Utils.GUI.Component.MessageBox;

import ClavarChat.Utils.GUI.Component.ChatMessage.ChatMessage;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MessageBox extends VBox
{
    private ChatMessage last;

    public MessageBox()
    {
        this.last = null;

        this.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setSpacing(20);
        this.setFillWidth(true);
    }

    public void createMessage(int userId, String pseudo, Image image, String text, boolean reverse)
    {
        ChatMessage chatMessage = new ChatMessage(userId, pseudo, image, reverse);
        chatMessage.addMessage(text);

        this.getChildren().add(chatMessage);
        this.last = chatMessage;
    }

    public void addMessage(int userId, String pseudo, Image image, String text, boolean reverse)
    {
        if (last != null)
        {
            if (this.last.getUserId() == userId)
            {
                this.last.addMessage(text);
            }
            else
            {
                this.createMessage(userId, pseudo, image, text, reverse);
            }
        }
        else
        {
            this.createMessage(userId, pseudo, image, text, reverse);
        }
    }
}
