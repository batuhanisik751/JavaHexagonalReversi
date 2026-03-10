package cs3500.reversi.strategy;

import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

/**
 * A recursive minimax strategy with configurable search depth.
 * Evaluates moves by recursively simulating play for both sides
 * and choosing the move that maximizes the score difference.
 */
public class DeepMiniMax implements IReversiStrategies {
  private final int maxDepth;

  /**
   * Creates a DeepMiniMax strategy with the given search depth.
   * @param maxDepth the maximum number of plies to search ahead.
   */
  public DeepMiniMax(int maxDepth) {
    this.maxDepth = maxDepth;
  }

  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> validMoves = StrategyUtils.getValidMoves(model, player);
    Coordinate bestMove = null;
    int bestScore = Integer.MIN_VALUE;
    for (Coordinate move : validMoves) {
      IReversiModel copy = model.copyModel();
      copy.move(move.getRow(), move.getCol(), player);
      int score = minimax(copy, maxDepth - 1, false, player);
      if (score > bestScore) {
        bestScore = score;
        bestMove = move;
      }
    }
    StrategyUtils.executeOrPass(model, bestMove, player);
  }

  private int minimax(IReversiModel model, int depth, boolean maximizing, Player player) {
    if (depth == 0 || model.gameOver()) {
      return model.getScore(player) - model.getOpponentScore(player);
    }
    Player current = model.getCurrentTurn();
    List<Coordinate> moves = StrategyUtils.getValidMoves(model, current);
    if (moves.isEmpty()) {
      IReversiModel copy = model.copyModel();
      copy.passTurn();
      return minimax(copy, depth - 1, !maximizing, player);
    }
    if (maximizing) {
      int best = Integer.MIN_VALUE;
      for (Coordinate move : moves) {
        IReversiModel copy = model.copyModel();
        copy.move(move.getRow(), move.getCol(), current);
        best = Math.max(best, minimax(copy, depth - 1, false, player));
      }
      return best;
    } else {
      int best = Integer.MAX_VALUE;
      for (Coordinate move : moves) {
        IReversiModel copy = model.copyModel();
        copy.move(move.getRow(), move.getCol(), current);
        best = Math.min(best, minimax(copy, depth - 1, true, player));
      }
      return best;
    }
  }
}
