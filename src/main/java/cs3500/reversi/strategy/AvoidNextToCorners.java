package cs3500.reversi.strategy;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a IReversiStrategies where the Spaces next to the corners of the board are avoided.
 * If there are only Spaces next to corners available as valid moves, the upper left-most will
 * be chosen.
 */
public class AvoidNextToCorners implements IReversiStrategies {

  /**
   * Chooses and plays the next move, using the strategy of avoiding Spaces next to corners
   * for the given Player.
   * @param model  the reversi game being played.
   * @param player the player the move is for.
   */
  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> allMoves = StrategyUtils.getValidMoves(model, player);
    List<Coordinate> filteredMoves = new ArrayList<>();
    for (Coordinate move : allMoves) {
      if (!isNextToCorner(move.getRow(), move.getCol(), model)) {
        filteredMoves.add(move);
      }
    }
    Coordinate bestMove = filteredMoves.isEmpty() ? null : filteredMoves.get(0);
    StrategyUtils.executeOrPass(model, bestMove, player);
  }


  /**
   * Checks if a given Space is next to a corner.
   *
   * @param row the row of the Space.
   * @param col column of the Space.
   * @param model the IReversiModel that is being looked at.
   * @return true if the Space is next to a corner, false if not.
   */
  private boolean isNextToCorner(int row, int col, IReversiModel model) {
    // checks top and bottom row
    if (row == 0 || row == model.getBoard().size() - 1) {
      return (col == 1 || col == model.getRow(row).size() - 2);
      // checks rows next to top and bottom rows
    } else if (row == 1 || row == model.getBoard().size() - 2) {
      return (col == 0 || col == 1
              || col == model.getRow(row).size() - 1 || col == model.getRow(row).size() - 2);
      // checks middle row (the widest row)
    } else if (row == model.getBoardSize() - 1) {
      return (col == 1 || col == model.getRow(row).size() - 2);
      // checks rows next to the middle row
    } else if (row == model.getBoardSize() || row == model.getBoardSize() - 2) {
      return (col == 0 || col == model.getRow(row).size() - 1);
    }
    return false;
  }
}
