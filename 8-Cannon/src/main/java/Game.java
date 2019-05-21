import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Class that controls the game */
public class Game extends Application {
    private static final double WIDTH = 800;
    private static final double HEIGHT = 640;
    private static final double CANNON_POSITION = 200;
    private static final double TARGET_POSITION = 800;
    private static final double TARGET_RADIUS = 10;
    private static final double WIDTH_IN_METERS = 1000;
    private static final int PEAKS_COUNT = 20;
    private static final int SEED = 10;
    private static final double[] PROJECTILE_POWERS = {0.1, 0.2, 0.3, 0.5, 0.7};
    private static final String GAME_NAME = "Scorched earth";

    /** Launches the application */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /** Starts the animation */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(GAME_NAME);
        primaryStage.setResizable(false);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        var root = new Group();
        var scene = new Scene(root);
        primaryStage.setScene(scene);
        var canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        var graphicsContext = canvas.getGraphicsContext2D();
        Set<KeyCode> keys = new HashSet<>();
        scene.setOnKeyPressed(event -> keys.add(event.getCode()));
        new AnimationTimer() {

            List<Explosion> explosions = new ArrayList<>();
            List<Projectile> projectiles = new ArrayList<>();
            int projectileType = 0;
            Renderer renderer = new Renderer(graphicsContext, WIDTH, HEIGHT, WIDTH_IN_METERS);
            Landscape landscape = new Landscape(PEAKS_COUNT, renderer.getWidthInMeters(), renderer.getHeightInMeters(), SEED);
            Cannon cannon = new Cannon(landscape, CANNON_POSITION);
            Target target = new Target(TARGET_POSITION, TARGET_RADIUS, landscape);

            /** {@inheritDoc} */
            @Override
            public void handle(long now) {
                renderer.clear();
                if (keys.contains(KeyCode.LEFT)) {
                    cannon.moveLeft();
                }
                if (keys.contains(KeyCode.RIGHT)) {
                    cannon.moveRight();
                }
                if (keys.contains(KeyCode.UP)) {
                    cannon.turnLeft();
                }
                if (keys.contains(KeyCode.DOWN)) {
                    cannon.turnRight();
                }
                if (keys.contains(KeyCode.ENTER)) {
                    projectiles.add(cannon.shoot(PROJECTILE_POWERS[projectileType]));
                }
                if (keys.contains(KeyCode.DIGIT1)) {
                    projectileType = 0;
                }
                if (keys.contains(KeyCode.DIGIT2)) {
                    projectileType = 1;
                }
                if (keys.contains(KeyCode.DIGIT3)) {
                    projectileType = 2;
                }
                if (keys.contains(KeyCode.DIGIT4)) {
                    projectileType = 3;
                }
                if (keys.contains(KeyCode.DIGIT5)) {
                    projectileType = 4;
                }
                keys.clear();
                projectiles.forEach(Projectile::move);
                var iterator = projectiles.iterator();
                while (iterator.hasNext()) {
                    var current = iterator.next();
                    if (!current.isAlive()) {
                        explosions.add(current.createExplosion());
                        iterator.remove();
                    }
                }
                explosions.removeIf(current -> !current.isAlive());
                renderer.fillBackground(Color.LIGHTBLUE);
                landscape.render(renderer);
                target.render(renderer);
                cannon.render(renderer);
                projectiles.forEach(projectile -> projectile.render(renderer));
                explosions.forEach(explosion -> explosion.render(renderer));
                renderer.writeText("1-5 to select type");
                for (var explosion : explosions) {
                    if (explosion.isTargetDestroyed(target)) {
                        renderer.clear();
                        renderer.writeText("Game over");
                        this.stop();
                    }
                }
            }
        }.start();
        primaryStage.show();
    }
}
