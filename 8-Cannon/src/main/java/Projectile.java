import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/** Class represents a projectile */
class Projectile implements Renderable {
    @NotNull
    private static final Point2D GRAVITY = new Point2D(0, -0.5);
    private static final double VELOCITY_MULTIPLIER = 30;
    private static final double RADIUS_MULTIPLIER = 15;
    private static final double EXPLOSION_MULTIPLIER = 5;
    private static final int EXPLOSION_STAGES = 20;
    @NotNull
    private Point2D center;
    @NotNull
    private Point2D velocity;
    @NotNull
    private Landscape landscape;
    private double radius = 1;
    private boolean isAlive = true;
    private double powerFactor;

    /** Creates a projectile with given center, direction and power factor at given landscape */
    Projectile(@NotNull Point2D center, @NotNull Point2D vector, double powerFactor, @NotNull Landscape landscape) {
        this.center = center;
        this.velocity = vector.normalize().multiply((1 - powerFactor) * VELOCITY_MULTIPLIER);
        this.landscape = landscape;
        this.powerFactor = powerFactor;
        radius = powerFactor * RADIUS_MULTIPLIER;
    }

    /** Calculates the next position of the projectile */
    void move() {
        center = center.add(velocity);
        velocity = velocity.add(GRAVITY);
        if (center.getY() <= landscape.getHeightAt(center.getX())) {
            isAlive = false;
        }
    }

    /** {@inheritDoc} */
    public void render(@NotNull Renderer renderer) {
        renderer.drawCircle(center, radius, Color.BLACK);
    }

    /** Returns if object is still alive */
    boolean isAlive() {
        return isAlive;
    }

    /** Creates an explosion at the place of the projectile */
    @NotNull
    Explosion createExplosion() {
        return new Explosion(center, EXPLOSION_MULTIPLIER * powerFactor, EXPLOSION_STAGES);
    }

}
