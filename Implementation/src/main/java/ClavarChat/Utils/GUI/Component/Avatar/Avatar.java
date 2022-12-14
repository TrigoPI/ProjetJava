package ClavarChat.Utils.GUI.Component.Avatar;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class Avatar extends VBox
{
    public Avatar(Image image, double radius)
    {
        super();

        ImageView view = new ImageView(image);
        Circle clippingShape = new Circle(radius);

        double diameter = radius * 2.0;
        double ratio = image.getWidth() / image.getHeight();
        double width = diameter * ratio;

        this.getChildren().add(view);
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        this.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        this.setPrefSize(diameter, diameter);

        view.setPreserveRatio(true);
        view.setFitWidth(width);

        double w = view.getBoundsInParent().getWidth();
        double h = view.getBoundsInParent().getHeight();

        clippingShape.setLayoutX(w / 2.0);
        clippingShape.setLayoutY(h / 2.0);

        view.setClip(clippingShape);
    }
}
