import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/** Class represents an explosion */
class Explosion implements Renderable {
    @NotNull
    private Point2D center;
    private double radius;
    private boolean isAlive = true;
    private int currentStage = 0;
    private int stages;

    /** Creates an explosion at given position with given step radius and given amount of steps */
    Explosion(@NotNull Point2D center, double radius, int stages) {
        this.center = center;
        this.radius = radius;
        this.stages = stages;
    }

    /** {@inheritDoc} */
    public void render(@NotNull Renderer renderer) {
        currentStage++;
        renderer.drawCircle(center, radius * currentStage, Color.RED);
        if (currentStage == stages) {
            isAlive = false;
        }
    }

    /** Returns if object is still alive */
    boolean isAlive() {
        return isAlive;
    }

    /** Returns if this explosion touched the target */
    boolean isTargetDestroyed(@NotNull Target target) {
        return target.getCenter().distance(center) <= radius * currentStage;
    }
}
