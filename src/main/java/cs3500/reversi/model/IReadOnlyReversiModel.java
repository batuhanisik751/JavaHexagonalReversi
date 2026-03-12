package cs3500.reversi.model;

import java.util.List;

/**
 * Represents an interface for a read-only Reversi model.
 */
public interface IReadOnlyReversiModel {
  /**
   * Gets the size of this model's board.
   * @return size of the board.
   */
  int getBoardSize();

  /**
   * Gets the Player that is at the specified Space.
   * @param numRow row of the Space.
   * @param numCol column of the Space.
   * @return the Player at the given Space, null if no player.
   */
  Player getSpaceContent(int numRow, int numCol);

  /**
   * Gets the model.ISpace at the specified location.
   * @param numRow the row of the model.ISpace.
   * @param numCol the column of the model.ISpace.
   * @return the model.ISpace at the coordinates.
   */
  ISpace getSpace(int numRow, int numCol);

  /**
   * Checks if move is possible to the given coordinates.
   * @param row the row of the space to be filled.
   * @param col the column of the space to be filled.
   * @return true if the move is valid.
   */
  boolean isValidMove(int row, int col, Player player);

  /**
   * Gets the current score for the model.Player,
   * (score is the number of Spaces the model.Player has).
   * @param player the model.Player to get the score for.
   * @return the number of Spaces filled by the model.Player.
   */
  int getScore(Player player);

  /**
   * Checks if there are no more valid moves for any player.
   * @return true if there are no more moves left.
   */
  boolean noValidMoves(Player player);

  /**
   * Checks if the game is over by no more valid move or by the board being filled.
   * @return true if the game is over.
   */
  boolean gameOver();

  /**
   * Gets the current turn of the model.
   * @return the model.Player whose turn it is.
   */
  Player getCurrentTurn();

  /**
   * Gets a copy of the board of the model.
   * @return copy of the board of the model.
   */
  List<List<ISpace>> getBoard();

  /**
   * Gets the row of the model as a list of ISpace.
   * @param numRow the row number that is to be returned.
   * @return the row as a list of ISpace
   */
  List<ISpace> getRow(int numRow);

  /**
   * Gets the score of the opponent of the given player.
   * @param player the Player to check the opponents score of.
   * @return number of Spaces claimed by the given player's opponent.
   */
  int getOpponentScore(Player player);

  /**
   * Gets the board shape used by this model.
   * @return the board shape.
   */
  IBoardShape getBoardShape();
}
