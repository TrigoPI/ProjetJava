package Application.JavaFXComponents.Animation.Floating;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.shape.Shape;
import javafx.util.Duration;


public class Floating extends Transition
{
    private final Shape shape;
    private final int max;
    private final double offset;

    public Floating(Shape shape, int max, double offset, int duration)
    {
        this.shape = shape;
        this.max = max;
        this.offset = offset;

        this.setCycleCount(INDEFINITE);
        this.setCycleDuration(Duration.millis(duration));
        this.setInterpolator(Interpolator.LINEAR);
    }

    @Override
    protected void interpolate(double t)
    {
        double w = Math.PI * 2;
        double T = t + this.offset;
        double y = Math.sin(w * T) * max;
        this.shape.setTranslateY(y);
    }
}
