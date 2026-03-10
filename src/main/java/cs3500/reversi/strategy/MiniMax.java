package cs3500.reversi.strategy;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

import java.util.List;

/**
 * Represents an IReversiStrategies where a move is made that will lower the maximum number
 * of spaces that the opponent will be able to claim (score) in the next turn. In the case of
 * a tie, the upper left-most valid move will be made.
 */
public class MiniMax implements IReversiStrategies {

  private final IReversiStrategies opponentStrategy;

  /**
   * Creates a MiniMax strategy that assumes the opponent uses the given strategy.
   * @param opponentStrategy the strategy to simulate for the opponent.
   */
  public MiniMax(IReversiStrategies opponentStrategy) {
    this.opponentStrategy = opponentStrategy;
  }

  /**
   * Creates a MiniMax strategy assuming the opponent maximizes captured pieces.
   */
  public MiniMax() {
    this(new AsManyPiecesAsPossible());
  }

  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> validMoves = StrategyUtils.getValidMoves(model, player);
    int lowestScore = Integer.MAX_VALUE;
    Coordinate bestMove = null;
    IReversiModel copyModel = model.copyModel();
    for (Coordinate move : validMoves) {
      IReversiModel copyOfCopy = copyModel.copyModel();
      copyOfCopy.move(move.getRow(), move.getCol(), copyOfCopy.getCurrentTurn());
      opponentStrategy.chooseNextMove(copyOfCopy, copyOfCopy.getCurrentTurn());
      if (copyOfCopy.getOpponentScore(copyOfCopy.getCurrentTurn()) < lowestScore) {
        lowestScore = copyOfCopy.getOpponentScore(copyOfCopy.getCurrentTurn());
        bestMove = move;
      }
    }
    StrategyUtils.executeOrPass(model, bestMove, model.getCurrentTurn());
  }
}
