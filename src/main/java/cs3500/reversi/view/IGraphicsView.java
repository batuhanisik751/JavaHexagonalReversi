package cs3500.reversi.view;

import java.io.File;
import java.util.List;

import cs3500.reversi.controller.ViewListener;
import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.Coordinate;

/**
 * Represents the interface for a graphical view of the Reversi game.
 */
public interface IGraphicsView {

  /**
   * Make the view visible.
   */
  void makeVisible();

  /**
   * Sets the listener that receives user actions (moves, passes) from this view.
   * @param listener the controller that handles user actions.
   */
  void setViewListener(ViewListener listener);

  /**
   * Updates the view with the current state of the game.
   */
  void refresh();

  /**
   * Shows a message indicating a player is out-of-turn.
   */
  void outOfTurnMessage();

  /**
   * Shows a message indicating the attempted move is invalid.
   */
  void invalidMoveMessage();

  /**
   * Sends a message displaying that the game is over, along with corresponding player scores.
   * @param blackScore score of Player.BLACK
   * @param whiteScore score of Player.WHITE
   */
  void gameOver(int blackScore, int whiteScore);

  /**
   * Sends a message displaying who holds the current turn.
   */
  void playerTurn();

  /**
   * Highlights the last move on the board — the placed piece and any flipped pieces.
   * @param placedRow row of the placed piece, or -1 to clear highlights.
   * @param placedCol column of the placed piece, or -1 to clear highlights.
   * @param flipped list of coordinates of pieces that were flipped.
   */
  void highlightLastMove(int placedRow, int placedCol, List<Coordinate> flipped);

  /**
   * Shows a message indicating that undo is not available.
   */
  void undoNotAvailableMessage();

  /**
   * Updates the move history display with the given records.
   * @param records the list of move records to display.
   */
  void updateHistory(List<MoveRecord> records);

  /**
   * Shows a message indicating the game was saved successfully.
   */
  void showSaveSuccess();

  /**
   * Shows a message indicating the game was loaded successfully.
   */
  void showLoadSuccess();

  /**
   * Shows an error message related to file operations.
   * @param message the error message to display.
   */
  void showFileError(String message);

  /**
   * Schedules an action to run after a delay on the UI thread.
   * Used for AI move delays without blocking the UI.
   * @param action the action to run.
   * @param delayMs the delay in milliseconds.
   */
  default void scheduleDelayed(Runnable action, int delayMs) {
    action.run();
  }

  /**
   * Runs an action on the UI thread. Used for deferring modal dialogs
   * so that the current callback can finish first.
   * @param action the action to run on the UI thread.
   */
  default void runOnUIThread(Runnable action) {
    action.run();
  }

  /**
   * Shows a file chooser for saving and returns the selected file, or null if cancelled.
   * @return the selected file, or null.
   */
  default File showSaveFileChooser() {
    return null;
  }

  /**
   * Shows a file chooser for loading and returns the selected file, or null if cancelled.
   * @return the selected file, or null.
   */
  default File showLoadFileChooser() {
    return null;
  }

  /**
   * Sets a status message displayed in the view (e.g. connection status for network play).
   * @param message the status message to display, or null to clear.
   */
  default void setStatusMessage(String message) {
    // no-op by default
  }
}
