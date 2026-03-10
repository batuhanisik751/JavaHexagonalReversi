package cs3500.reversi.view;

import java.util.List;

import cs3500.reversi.controller.ViewListener;
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
}
