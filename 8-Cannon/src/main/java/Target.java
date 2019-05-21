import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/** Class representing a target */
class Target implements Renderable {
    private Point2D center;
    private double radius;

    /** Creates a target at given position with given radius at given landscape */
    Target(double x, double radius, Landscape landscape) {
        this.center = new Point2D(x, landscape.getHeightAt(x));
        this.radius = radius;
    }

    /** {@inheritDoc} */
    public void render(Renderer renderer) {
        renderer.drawCircle(center, radius, Color.BLUE);
        renderer.drawCircle(center, radius * 2.0 / 3.0, Color.YELLOW);
        renderer.drawCircle(center, radius / 3.0, Color.RED);
    }

    /** Returns a center of target */
    Point2D getCenter() {
        return center;
    }
}
