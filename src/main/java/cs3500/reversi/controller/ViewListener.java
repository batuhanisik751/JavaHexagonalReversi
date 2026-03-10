package cs3500.reversi.controller;

/**
 * Listener interface for view events. Controllers implement this to receive
 * user actions from the graphical view without the view needing to know
 * about the model.
 */
public interface ViewListener {

  /**
   * Called when the user attempts to make a move at the given coordinates.
   * @param row the row of the selected space.
   * @param col the column of the selected space.
   */
  void onMove(int row, int col);

  /**
   * Called when the user passes their turn.
   */
  void onPass();

  /**
   * Called when the user requests to undo their last move.
   */
  void onUndo();

  /**
   * Called when the user requests to save the game.
   */
  void onSave();

  /**
   * Called when the user requests to load a saved game.
   */
  void onLoad();
}
