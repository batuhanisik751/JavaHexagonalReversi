package cs3500.reversi.strategy;

import java.util.ArrayList;
import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

/**
 * Shared utility methods for Reversi strategy implementations.
 */
public final class StrategyUtils {

  private StrategyUtils() {
    // utility class, not instantiable
  }

  /**
   * Collects all valid moves for the given player on the board.
   * @param model the game state to query.
   * @param player the player to find moves for.
   * @return list of valid move coordinates, in top-to-bottom left-to-right order.
   */
  public static List<Coordinate> getValidMoves(IReadOnlyReversiModel model, Player player) {
    List<Coordinate> validMoves = new ArrayList<>();
    for (int row = 0; row < model.getBoard().size(); row++) {
      for (int col = 0; col < model.getRow(row).size(); col++) {
        if (model.isValidMove(row, col, player)) {
          validMoves.add(new Coordinate(row, col));
        }
      }
    }
    return validMoves;
  }

  /**
   * Executes the given move on the model, or passes the turn if no move is provided.
   * @param model the game to act on.
   * @param move the move to execute, or null to pass.
   * @param player the player making the move.
   */
  public static void executeOrPass(IReversiModel model, Coordinate move, Player player) {
    if (move != null) {
      model.move(move.getRow(), move.getCol(), player);
    } else {
      model.passTurn();
    }
  }
}
