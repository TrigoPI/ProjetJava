package Application.JavaFXComponents.Component.MessageBox;

import Application.JavaFXComponents.Component.ChatMessage.TextMessage;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MessageBox extends VBox
{
    private TextMessage last;
    private boolean typing;

    public MessageBox()
    {
        this.typing = false;
        this.last = null;

        this.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setSpacing(20);
        this.setFillWidth(true);
    }

    public void updateDisplay(int userId, String pseudo, Image image)
    {
        for (Node node : this.getChildren())
        {
            TextMessage message = (TextMessage)node;
            if (message.getUserId() == userId) message.update(pseudo, image);
        }
    }

    public void removeTyping()
    {
        if (!this.typing) return;
        if (this.last == null) return;
        if (this.getChildren().isEmpty()) return;

        this.last.removeLast();

        if (this.last.isEmpty())
        {
            int index = this.getChildren().size() - 1;
            this.getChildren().remove(index);
            this.last = null;
        }
        else
        {
            int index = this.getChildren().size() - 1;
            this.last = (TextMessage)this.getChildren().get(index);
        }

        this.typing = false;
    }

    public void addTyping(int userId, String pseudo, Image image)
    {
        if (typing) return;

        if (this.last == null)
        {
            this.createTyping(userId, pseudo, image);
            this.typing = true;
            return;
        }

        if (this.last.getUserId() == userId)
        {
            this.last.addTyping();
        }
        else
        {
            this.createTyping(userId, pseudo, image);
        }

        this.typing = true;
    }

    public void addMessage(int userId, String pseudo, Image image, String text, boolean reverse)
    {
        if (this.last == null)
        {
            this.createMessage(userId, pseudo, image, text, reverse);
            return;
        }

        if (this.last.getUserId() == userId)
        {
            this.last.addMessage(text);
        }
        else
        {
            this.createMessage(userId, pseudo, image, text, reverse);
        }
    }

    private void createMessage(int userId, String pseudo, Image image, String text, boolean reverse)
    {
        TextMessage textMessage = new TextMessage(userId, pseudo, image, reverse);
        textMessage.addMessage(text);

        this.getChildren().add(textMessage);
        this.last = textMessage;
    }
    private void createTyping(int userId, String pseudo, Image image)
    {
        TextMessage textMessage = new TextMessage(userId, pseudo, image, true);
        textMessage.addTyping();

        this.getChildren().add(textMessage);
        this.last = textMessage;
    }
}
