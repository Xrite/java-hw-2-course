import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Class that runs the game */
public class Main extends Application implements GameContext {

    private ToggleButton[][] buttons;
    private GameController gameController;
    private PauseTransition pauseTransition;

    /** launches the game */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //var parameters = getParameters().getRaw();
        int n = 2;//Integer.parseInt(parameters.get(0));
        buttons = new ToggleButton[n][n];
        primaryStage.setScene(createGame(n));
        primaryStage.setTitle("GameController");
        primaryStage.setMinHeight(n * 30);
        primaryStage.setMinWidth(n * 30);
        primaryStage.setHeight(n * 100);
        primaryStage.setWidth(n * 100);
        gameController = new GameController(n, this);

        primaryStage.show();
    }

    /** Sets up a scene */
    private Scene createGame(int n) {
        var gridPane = new GridPane();
        var columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100.0 / n);
        var rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(100.0 / n);
        for (int i = 0; i < n; i++) {
            gridPane.getColumnConstraints().add(columnConstraints);
            gridPane.getRowConstraints().add(rowConstraints);
            for (int j = 0; j < n; j++) {
                int copyI = i;
                int copyJ = j;
                var button = new ToggleButton();
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                button.setOnAction(event -> {
                    gameController.select(new GameController.Position(copyI, copyJ));
                });
                button.setFocusTraversable(true);
                gridPane.add(button, i, j);
                buttons[i][j] = button;
            }
        }
        return new Scene(gridPane);
    }

    /** {@inheritDoc}*/
    @Override
    public void onFirstItemSelected(GameController.Position position) {
        buttons[position.i][position.j].setText("?");
        if (pauseTransition != null) {
            pauseTransition.stop();
            pauseTransition.getOnFinished().handle(null);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCorrectMatch(GameController.Position first, GameController.Position second, String firstValue, String secondValue) {
        buttons[first.i][first.j].setText(firstValue);
        buttons[second.i][second.j].setText(secondValue);
        buttons[first.i][first.j].setDisable(true);
        buttons[second.i][second.j].setDisable(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onIncorrectMatch(GameController.Position first, GameController.Position second, String firstValue, String secondValue) {
        buttons[first.i][first.j].setText(firstValue);
        buttons[second.i][second.j].setText(secondValue);
        pauseTransition = new PauseTransition(new Duration(3000));
        pauseTransition.setOnFinished(event -> {
            buttons[first.i][first.j].setSelected(false);
            buttons[second.i][second.j].setSelected(false);
            buttons[first.i][first.j].setText("");
            buttons[second.i][second.j].setText("");
        });
        pauseTransition.play();
    }

    /** {@inheritDoc} */
    @Override
    public void onGameEnded() {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setOnCloseRequest(event -> {
            Platform.exit();
        });
        alert.setTitle("GameController over");
        alert.setContentText("Epic win!");
        alert.show();

    }
}
