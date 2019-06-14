import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

/** Class that draw game objects */
class Renderer {
    private static final double TEXT_X = 50;
    private static final double TEXT_Y = 50;
    private static final double FONT_SIZE = 30;
    private static final int POLYGON_SIZE = 4;
    @NotNull
    private GraphicsContext context;
    private double screenWidth;
    private double screenHeight;
    private double widthInMeters;
    private double heightInMeters;
    private double scaleFactor;

    /** Creates renderer with given context, screen width and heigth and scales width to be in meters */
    Renderer(@NotNull GraphicsContext context, double screenWidth, double screenHeight, double widthInMeters) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.context = context;
        this.widthInMeters = widthInMeters;
        scaleFactor = screenWidth / widthInMeters;
        heightInMeters = screenHeight / scaleFactor;
    }

    private double transformX(double x) {
        return x * scaleFactor;
    }

    private double transformY(double y) {
        return (heightInMeters - y) * scaleFactor;
    }

    private double transform(double value) {
        return value * scaleFactor;
    }

    /** Draws a circle with given center, radius and color */
    void drawCircle(@NotNull Point2D center, double radius, @NotNull Paint paint) {
        double r = transform(radius);
        double x = transformX(center.getX()) - r;
        double y = transformY(center.getY()) - r;
        context.setFill(paint);
        context.fillOval(x, y, 2 * r, 2 * r);
    }

    /** Draws a black line with given begin and end */
    void drawLine(@NotNull Point2D begin, @NotNull Point2D end) {
        context.setStroke(Color.BLACK);
        context.strokeLine(transformX(begin.getX()), transformY(begin.getY()),
                transformX(end.getX()), transformY(end.getY()));
    }

    /** Fills area under given segment in given color */
    void fillUnderLine(@NotNull Point2D begin, @NotNull Point2D end, @NotNull Paint paint) {
        context.setFill(paint);
        context.fillPolygon(
                new double[]{transformX(begin.getX()), transformX(end.getX()), transformX(end.getX()), transformX(begin.getX())},
                new double[]{transformY(begin.getY()), transformY(end.getY()), transformY(0), transformY(0)},
                POLYGON_SIZE);
    }

    /** Returns width in meters */
    double getWidthInMeters() {
        return widthInMeters;
    }

    /** Returns height in meters */
    double getHeightInMeters() {
        return heightInMeters;
    }

    /** Writes the text at the top left corner */
    void writeText(@NotNull String text) {
        context.setFill(Color.BLACK);
        context.setStroke(Color.BLACK);
        context.setFont(Font.font(FONT_SIZE));
        context.fillText(text, TEXT_X, TEXT_Y);
    }

    /** Fills the background in given color */
    void fillBackground(@NotNull Paint paint) {
        context.setFill(paint);
        context.fillRect(0, 0, screenWidth, screenHeight);
    }

    /** Clears the area */
    void clear() {
        context.clearRect(0, 0, screenWidth, screenHeight);
    }
}
