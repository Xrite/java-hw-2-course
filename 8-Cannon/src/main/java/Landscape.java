import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Random;

/** Class represents a landscape */
class Landscape implements Renderable {
    private static final double HEIGHT_MULTIPLIER = 0.5;
    private Point2D[] peaks;

    /**
     * Creates a landscape with given amount of peaks, with given
     * height and width in meters and uses the seed to generate landscape
     */
    Landscape(int peaksCount, double width, double height, long seed) {
        var xCoords = new double[peaksCount + 2];
        peaks = new Point2D[peaksCount + 2];
        xCoords[0] = -0.1;
        xCoords[peaksCount + 1] = 1.1;
        var random = new Random(seed);
        for (int i = 1; i <= peaksCount; i++) {
            xCoords[i] = random.nextDouble();
        }
        Arrays.sort(xCoords);
        for (int i = 0; i < peaksCount + 2; i++) {
            peaks[i] = new Point2D(xCoords[i] * width, random.nextDouble() * height * HEIGHT_MULTIPLIER);
        }
    }

    /** Return height at given position */
    double getHeightAt(double x) {
        for (int i = 0; i < peaks.length - 1; i++) {
            if (peaks[i].getX() <= x && x < peaks[i + 1].getX()) {
                double factor = (x - peaks[i].getX()) / (peaks[i + 1].getX() - peaks[i].getX());
                var vector = peaks[i + 1].subtract(peaks[i]).multiply(factor);
                return peaks[i].add(vector).getY();
            }
        }
        return 0;
    }

    /** {@inheritDoc} */
    public void render(Renderer renderer) {
        for (int i = 0; i < peaks.length - 1; i++) {
            renderer.drawLine(peaks[i], peaks[i + 1]);
            renderer.fillUnderLine(peaks[i], peaks[i + 1], Color.DARKGREEN);
        }
    }
}
