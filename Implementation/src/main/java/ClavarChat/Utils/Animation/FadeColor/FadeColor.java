package ClavarChat.Utils.Animation.FadeColor;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class FadeColor extends Transition
{
    private Color a;
    private Color b;
    private Region region;

    public FadeColor(Color a, Color b, Region region, int duration)
    {
        this.a = a;
        this.b = b;

        this.region = region;

        this.setCycleDuration(Duration.millis(duration));
        this.setInterpolator(Interpolator.LINEAR);
    }

    @Override
    protected void interpolate(double t)
    {
        double r = (1 - t) * this.a.getRed()     + t * this.b.getRed();
        double g = (1 - t) * this.a.getGreen()   + t * this.b.getGreen();
        double b = (1 - t) * this.a.getBlue()    + t * this.b.getBlue();
        double a = (1 - t) * this.a.getOpacity() + t * this.b.getOpacity();

        this.region.setBackground(new Background(new BackgroundFill(new Color(r, g, b, a), CornerRadii.EMPTY, Insets.EMPTY)));
    }
}
