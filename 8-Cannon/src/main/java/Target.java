import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/** Class representing a target */
class Target implements Renderable {
    private static final double BLUE_FRACTION = 1;
    private static final double YELLOW_FRACTION = 2.0 / 3.0;
    private static final double RED_FRACTION = 1.0 / 3.0;
    @NotNull
    private Point2D center;
    private double radius;

    /** Creates a target at given position with given radius at given landscape */
    Target(double x, double radius, @NotNull Landscape landscape) {
        this.center = new Point2D(x, landscape.getHeightAt(x));
        this.radius = radius;
    }

    /** {@inheritDoc} */
    public void render(@NotNull Renderer renderer) {
        renderer.drawCircle(center, radius * BLUE_FRACTION, Color.BLUE);
        renderer.drawCircle(center, radius * YELLOW_FRACTION, Color.YELLOW);
        renderer.drawCircle(center, radius * RED_FRACTION, Color.RED);
    }

    /** Returns a center of target */
    @NotNull
    Point2D getCenter() {
        return center;
    }
}
