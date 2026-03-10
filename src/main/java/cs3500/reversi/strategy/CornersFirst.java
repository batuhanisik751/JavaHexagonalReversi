package cs3500.reversi.strategy;

import java.util.ArrayList;
import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

/**
 * Represents an IReversiStrategies where the corners of the board are prioritized
 * for making a move, if there are no corners available then the upper left-most
 * valid move will be made.
 */
public class CornersFirst implements IReversiStrategies {

  /**
   * Chooses and plays the next move, using the strategy of moving to corners for the given Player.
   * @param model  the reversi game being played.
   * @param player the player the move is for.
   */
  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> allMoves = StrategyUtils.getValidMoves(model, player);
    List<Coordinate> cornerMoves = new ArrayList<>();
    for (Coordinate move : allMoves) {
      if (isCorner(move.getRow(), move.getCol(), model)) {
        cornerMoves.add(move);
      }
    }
    Coordinate bestMove;
    if (!cornerMoves.isEmpty()) {
      bestMove = cornerMoves.get(0);
    } else if (!allMoves.isEmpty()) {
      bestMove = allMoves.get(0);
    } else {
      bestMove = null;
    }
    StrategyUtils.executeOrPass(model, bestMove, player);
  }

  /**
   * Checks if the given Space is a corner Space.
   * @param row the row of the Space
   * @param col column of the Space
   * @param model the IReversiModel that is being looked at.
   * @return true if the Space is at a corner, false if not.
   */
  private boolean isCorner(int row, int col, IReversiModel model) {
    // checks top, bottom, and middle row
    if (row == 0 || row == model.getBoard().size() - 1 || row == model.getBoardSize() - 1) {
      return (col == 0 || col == model.getRow(row).size() - 1);
    }
    return false;
  }
}
