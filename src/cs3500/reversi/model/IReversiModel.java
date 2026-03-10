package cs3500.reversi.model;

/**
 * Represents the Reversi game model.
 */
public interface IReversiModel extends IReadOnlyReversiModel {

  /**
   * Passes the current turn onto the next Player.
   */
  void passTurn();

  /**
   * Makes a move at the given location for the given Player.
   * Fills the space, flips opponent pieces, and advances the turn.
   * @param row the row of the space to be filled.
   * @param col the column of the space to be filled.
   * @param player the player making the move.
   * @return true if the move was executed.
   * @throws IllegalStateException if the move is invalid or out of turn.
   */
  boolean move(int row, int col, Player player);

  /**
   * Creates a deep copy of this model.
   * @return a new independent copy of this model.
   */
  IReversiModel copyModel();
}
