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
    private final Circle statusCircle;

    public Avatar(Image image, double radius, boolean connected)
    {
        super();

        this.statusCircle = new Circle(0.2 * radius);

        ImageView view = new ImageView(image);
        Circle clippingShape = new Circle(radius);

        double diameter = radius * 2.0;
        double ratio = image.getWidth() / image.getHeight();
        double width = diameter * ratio;

        this.setAlignment(Pos.CENTER);
        this.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        this.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        this.setPrefSize(diameter, diameter);
        this.getChildren().add(view);
        this.getChildren().add(statusCircle);

        view.setPreserveRatio(true);
        view.setFitWidth(width);

        double imageWidth  = view.getBoundsInParent().getWidth();
        double imageHeight = view.getBoundsInParent().getHeight();
        double translate = radius * (1 + a);

        clippingShape.setLayoutX(imageWidth  / 2.0);
        clippingShape.setLayoutY(imageHeight / 2.0);

        statusCircle.setManaged(false);
        statusCircle.setStrokeWidth(4);
        statusCircle.setStroke(Colors.draculaOrchid);
        statusCircle.setTranslateX(translate);
        statusCircle.setTranslateY(translate);

        view.setClip(clippingShape);
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

    public void changeStrokeColor(Color color)
    {
        this.statusCircle.setStroke(color);
    }

    public void displayStatus(boolean b)
    {
        this.statusCircle.setVisible(b);
    }
}
