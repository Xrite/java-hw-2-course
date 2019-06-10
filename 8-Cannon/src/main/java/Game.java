import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

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
    private static final int DEFAULT_PROJECTILE_TYPE = 0;
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
        new GameAnimation(graphicsContext, DEFAULT_PROJECTILE_TYPE, keys).start();
        primaryStage.show();
    }

    private class GameAnimation extends AnimationTimer {
        @NotNull
        private List<Explosion> explosions = new ArrayList<>();
        @NotNull
        private List<Projectile> projectiles = new ArrayList<>();
        private int projectileType;
        @NotNull
        private Renderer renderer;
        @NotNull
        private Landscape landscape;
        @NotNull
        private Cannon cannon;
        @NotNull
        private Target target;
        @NotNull
        private Set<KeyCode> keys;

        private GameAnimation(@NotNull GraphicsContext graphicsContext, int defaultType, @NotNull Set<KeyCode> keys) {
            renderer = new Renderer(graphicsContext, WIDTH, HEIGHT, WIDTH_IN_METERS);
            landscape = new Landscape(PEAKS_COUNT, renderer.getWidthInMeters(), renderer.getHeightInMeters(), SEED);
            cannon = new Cannon(landscape, CANNON_POSITION);
            target = new Target(TARGET_POSITION, TARGET_RADIUS, landscape);
            projectileType = defaultType;
            this.keys = keys;
        }

        private void processKeys() {
            for (var key : keys) {
                switch (key) {
                    case LEFT:
                        cannon.moveLeft();
                        break;
                    case RIGHT:
                        cannon.moveRight();
                        break;
                    case UP:
                        cannon.turnLeft();
                        break;
                    case DOWN:
                        cannon.turnRight();
                        break;
                    case ENTER:
                        projectiles.add(cannon.shoot(PROJECTILE_POWERS[projectileType]));
                        break;
                    case DIGIT1:
                        projectileType = 0;
                        break;
                    case DIGIT2:
                        projectileType = 1;
                        break;
                    case DIGIT3:
                        projectileType = 2;
                        break;
                    case DIGIT4:
                        projectileType = 3;
                        break;
                    case DIGIT5:
                        projectileType = 4;
                        break;
                }
            }
            keys.clear();
        }

        private void processProjectiles() {
            projectiles.forEach(Projectile::move);
            var iterator = projectiles.iterator();
            while (iterator.hasNext()) {
                var current = iterator.next();
                if (!current.isAlive()) {
                    explosions.add(current.createExplosion());
                    iterator.remove();
                }
            }
        }

        private void processExplosions() {
            explosions.removeIf(current -> !current.isAlive());
        }

        private void endGame() {
            renderer.clear();
            renderer.writeText("Game over");
            this.stop();
        }

        private void renderExplosions() {
            for (var explosion : explosions) {
                if (explosion.isTargetDestroyed(target)) {
                    endGame();
                }
            }
        }

        private void render() {
            renderer.fillBackground(Color.LIGHTBLUE);
            landscape.render(renderer);
            target.render(renderer);
            cannon.render(renderer);
            projectiles.forEach(projectile -> projectile.render(renderer));
            explosions.forEach(explosion -> explosion.render(renderer));
            renderer.writeText("1-5 to select type");
            renderExplosions();
        }

        @Override
        public void handle(long now) {
            renderer.clear();
            processKeys();
            processProjectiles();
            processExplosions();
            render();
        }
    }
}
