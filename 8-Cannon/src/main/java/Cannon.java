import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/** Class representing a cannon */
class Cannon implements Renderable {
    private static final double CANNON_VELOCITY = 2.5;
    private static final double CANNON_RADIUS = 10;
    private static final double BARREL_LENGTH = 20;
    private static final double ANGULAR_SPEED = 0.03;
    private Point2D position;
    private double angle = Math.PI / 2;
    private Landscape landscape;

    /** Creates cannon at given landscape at given position */
    Cannon(Landscape landscape, double x) {
        position = new Point2D(x, landscape.getHeightAt(x));
        this.landscape = landscape;
    }

    /** Moves cannon to the right */
    void moveRight() {
        double newX = position.getX() + CANNON_VELOCITY;
        double newY = landscape.getHeightAt(newX);
        if(newX <= landscape.getMaxX()) {
            position = new Point2D(newX, newY);
        }
    }

    /** Moves cannon to the left */
    void moveLeft() {
        double newX = position.getX() - CANNON_VELOCITY;
        double newY = landscape.getHeightAt(newX);
        if(newX >= landscape.getMinX()) {
            position = new Point2D(newX, newY);
        }
    }

    /** Turns a cannon's barrel right */
    void turnRight() {
        angle -= ANGULAR_SPEED;
    }

    /** Turns a cannon's barrel left */
    void turnLeft() {
        angle += ANGULAR_SPEED;
    }

    /** Creates a projectile from barrel with given power factor */
    Projectile shoot(double powerFactor) {
        var barrelVector = new Point2D(Math.cos(angle), Math.sin(angle)).multiply(BARREL_LENGTH);
        return new Projectile(position.add(barrelVector), barrelVector.normalize().multiply(5), powerFactor, landscape);
    }

    /** {@inheritDoc} */
    public void render(Renderer renderer) {
        var barrelVector = new Point2D(Math.cos(angle), Math.sin(angle)).multiply(BARREL_LENGTH);
        renderer.drawLine(position, position.add(barrelVector));
        renderer.drawCircle(position, CANNON_RADIUS, Color.GREEN);
    }
}
