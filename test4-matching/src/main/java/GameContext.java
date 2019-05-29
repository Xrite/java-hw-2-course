/**
 * Interface for classes that can be context for gameController
 */
public interface GameContext {
    /** Action to do when first item was selected */
    void onFirstItemSelected(GameController.Position first);

    /**
     * Action to do whew correct match occurred
     * @param first position of first selected item
     * @param second position of second selected item
     * @param firstValue value of the first selected item
     * @param secondValue value of the second selected item
     * */
    void onCorrectMatch(GameController.Position first, GameController.Position second, String firstValue, String secondValue);

    /**
     * Action to do whew correct match occurred
     * @param first position of first selected item
     * @param second position of second selected item
     * @param firstValue value of the first selected item
     * @param secondValue value of the second selected item
     * */
    void onIncorrectMatch(GameController.Position first, GameController.Position second, String firstValue, String secondValue);

    /** Action to do when game ended */
    void onGameEnded();
}
