import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

/** Class that controls the game */
class GameController {

    private int[][] field;
    private boolean[][] guessed;
    private GameContext context;
    private Position firstSelected = null;
    private Position secondSelected = null;
    private int n;
    private int guessedCount = 0;


    /**
     * Creates a controller
     * @param n size of a game that must be even
     * @param context a context for controller
     * @throws IllegalArgumentException when n is odd or not positive
     */
    GameController(int n, GameContext context) throws IllegalArgumentException {
        this.context = context;
        this.n = n;
        if(n % 2 == 1 || n <= 0) {
            throw new IllegalArgumentException("N can't be ood");
        }
        var nums = new ArrayList<Integer>();
        var random = new Random();
        for(int i = 0; i < n * n / 2; i++) {
            var value = random.nextInt(n * n / 2);
            nums.add(value);
            nums.add(value);
        }
        Collections.shuffle(nums);
        field = new int[n][n];
        guessed = new boolean[n][n];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                field[i][j] = nums.get(i * n + j);
                guessed[i][j] = false;
            }
        }
    }

    private int getAt(Position pos) {
        return field[pos.i][pos.j];
    }


    /**
     * Signals the controller that item was selected
     * @param position position of selected item
     */
    void select(Position position) {
        if(guessed[position.i][position.j]) {
            return;
        }
        if(firstSelected == null) {
            firstSelected = position;
            context.onFirstItemSelected(position);
            return;
        }
        if(secondSelected == null) {
            if(firstSelected.equals(position)) {
                return;
            }
            secondSelected = position;
            if(getAt(firstSelected) == getAt(secondSelected)) {
                context.onCorrectMatch(firstSelected, secondSelected, Integer.toString(getAt(firstSelected)), Integer.toString(getAt(secondSelected)));
                guessed[firstSelected.i][firstSelected.j] = true;
                guessed[secondSelected.i][secondSelected.j] = true;
                guessedCount += 2;
                if(guessedCount == n * n) {
                    context.onGameEnded();
                }
            } else {
                context.onIncorrectMatch(firstSelected, secondSelected, Integer.toString(getAt(firstSelected)), Integer.toString(getAt(secondSelected)));
            }
            firstSelected = null;
            secondSelected = null;
        }
    }

    /** Simple class that represents a position of item */
    static class Position {
        final int i;
        final int j;

        /** Creates a position */
        Position(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return i == position.i &&
                    j == position.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j);
        }
    }


}

