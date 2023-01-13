package Application.JavaFXComponents.Component.MessageBox;

import Application.JavaFXComponents.Component.ChatMessage.TextMessage;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MessageBox extends VBox
{
    private TextMessage last;

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
        TextMessage textMessage = new TextMessage(userId, pseudo, image, reverse);
        textMessage.addMessage(text);

        this.getChildren().add(textMessage);
        this.last = textMessage;
    }

    public void updateDisplay(int userId, String pseudo, Image image)
    {
        for (Node node : this.getChildren())
        {
            TextMessage message = (TextMessage)node;
            if (message.getUserId() == userId) message.update(pseudo, image);
        }
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
