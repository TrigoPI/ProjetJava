package ClavarChat.Utils.GUI.Component.Avatar;

import ClavarChat.Utils.GUI.Colors.Colors;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Avatar extends VBox
{
    private static final double a = Math.sqrt(2.0) / 2.0;
    private final double radius;
    private final Circle statusCircle;
    private final Circle clippingShape;
    private final ImageView view;

    public Avatar(Image image, double radius, boolean connected)
    {
        super();

        this.radius = radius;
        this.statusCircle = new Circle(0.2 * radius);
        this.clippingShape = new Circle(radius);
        this.view = new ImageView(image);


        double diameter = radius * 2.0;
        double ratio = image.getWidth() / image.getHeight();
        double width = diameter * ratio;

        this.setAlignment(Pos.CENTER);
        this.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        this.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        this.setPrefSize(diameter, diameter);
        this.getChildren().add(view);
        this.getChildren().add(statusCircle);

        this.view.setPreserveRatio(true);
        this.view.setFitWidth(width);

        double imageWidth  = this.view.getBoundsInParent().getWidth();
        double imageHeight = this.view.getBoundsInParent().getHeight();
        double translate = radius * (1 + a);

        this.clippingShape.setLayoutX(imageWidth  / 2.0);
        this.clippingShape.setLayoutY(imageHeight / 2.0);

        statusCircle.setManaged(false);
        statusCircle.setStrokeWidth(4);
        statusCircle.setStroke(Colors.draculaOrchid);
        statusCircle.setTranslateX(translate);
        statusCircle.setTranslateY(translate);

        this.view.setClip(this.clippingShape);
        this.setStatus(connected);
    }

    public void setStatus(boolean connected)
    {
        if (connected)
        {
            statusCircle.setFill(Colors.limeSoap);
        }
        else
        {
            statusCircle.setFill(Colors.chiGong);
        }
    }

    public void changeImage(Image image)
    {
        this.view.setClip(null);
        this.view.setImage(image);

        double diameter = radius * 2.0;
        double ratio = image.getWidth() / image.getHeight();
        double width = diameter * ratio;

        this.view.setFitWidth(width);

        double imageWidth  = this.view.getBoundsInParent().getWidth();
        double imageHeight = this.view.getBoundsInParent().getHeight();

        this.clippingShape.setLayoutX(imageWidth  / 2.0);
        this.clippingShape.setLayoutY(imageHeight / 2.0);

        this.view.setClip(this.clippingShape);
    }

    public void changeStrokeColor(Color color)
    {
        this.statusCircle.setStroke(color);
    }

    public void displayStatus(boolean b)
    {
        this.statusCircle.setVisible(b);
    }
}
