package ClavarChat.Utils.GUI.Component.Discussion;

import ClavarChat.Utils.GUI.Animation.FadeColor.FadeColor;
import ClavarChat.Utils.GUI.Colors.Colors;
import ClavarChat.Utils.GUI.Component.Avatar.Avatar;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Discussion extends HBox
{
    private boolean selected;

    private final int userId;

    private final int conversationId;
    private final String pseudo;
    private final Avatar avatar;

    public Discussion(int conversationId, int userId, boolean connected, Image img, String pseudo, String text)
    {
        super();

        this.pseudo = pseudo;
        this.userId = userId;
        this.conversationId = conversationId;
        this.selected = false;
        this.avatar = new Avatar(img, 35, connected);

        VBox vBox = new VBox();
        Label pseudoLabel = new Label(pseudo);
        Label textLabel = new Label(text);

        pseudoLabel.getStyleClass().add("clvc-font-bold");

        textLabel.getStyleClass().add("clvc-font-12px");

        vBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
        vBox.setMinSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setPrefSize(120, Region.USE_COMPUTED_SIZE);
        vBox.setSpacing(20);


        this.setAlignment(Pos.CENTER_LEFT);
        this.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE);
        this.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE);
        this.setPrefSize(Region.USE_COMPUTED_SIZE, 120);
        this.setSpacing(15);
        this.getStyleClass().add("clvc-user-preview-container");
        this.getStyleClass().add("clvc-dracula-orchid");
        this.getStyleClass().add("clvc-cursor-hand");

        vBox.getChildren().add(pseudoLabel);
        vBox.getChildren().add(textLabel);

        this.getChildren().add(avatar);
        this.getChildren().add(vBox);

        this.setOnMouseEntered(this::onMouseEntered);
        this.setOnMouseExited(this::onMouseExited);
    }

    public int getUserId()
    {
        return this.userId;
    }

    public int getConversationId()
    {
        return this.conversationId;
    }

    public String getPseudo()
    {
        return this.pseudo;
    }

    public void select()
    {
        this.selected = true;
        this.getStyleClass().remove("clvc-dracula-orchid");
        this.getStyleClass().add("clvc-american-river");
    }

    public void deselect()
    {
        this.selected = false;
        this.getStyleClass().remove("clvc-american-river");
        this.getStyleClass().add("clvc-dracula-orchid");
    }

    private void onMouseEntered(MouseEvent event)
    {
        if (this.selected) return;

        Color startColor = (Color)this.getBackground().getFills().get(0).getFill();
        FadeColor fadeColor = new FadeColor(startColor, Colors.americanRiver, this, 200);
        fadeColor.playFromStart();
    }

    private void onMouseExited(MouseEvent event)
    {
        if (this.selected) return;

        Color startColor = (Color)this.getBackground().getFills().get(0).getFill();
        FadeColor fadeColor = new FadeColor(startColor, Colors.draculaOrchid, this, 200);
        fadeColor.playFromStart();
    }
}
