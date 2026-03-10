package cs3500.reversi.strategy;

import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

/**
 * Represents a IReversiStrategies where the move chosen is the move,
 * that claims the most possible pieces.
 */
public class AsManyPiecesAsPossible implements IReversiStrategies {

  /**
   * Chooses the next move for the given player in the Reversi model based on the strategy
   * of claiming as many pieces as possible.
   * @param model  The Reversi model representing the game state.
   * @param player The player for whom the move is chosen.
   */
  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> validMoves = StrategyUtils.getValidMoves(model, player);
    int highestScore = -1;
    Coordinate bestMove = null;
    IReversiModel copyModel = model.copyModel();
    for (Coordinate move : validMoves) {
      IReversiModel copyOfCopy = copyModel.copyModel();
      copyOfCopy.move(move.getRow(), move.getCol(), copyOfCopy.getCurrentTurn());
      if (copyOfCopy.getOpponentScore(copyOfCopy.getCurrentTurn()) > highestScore) {
        highestScore = copyOfCopy.getOpponentScore(copyOfCopy.getCurrentTurn());
        bestMove = move;
      }
    }
    StrategyUtils.executeOrPass(model, bestMove, model.getCurrentTurn());
  }
}
