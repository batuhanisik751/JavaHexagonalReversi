package cs3500.reversi.strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

/**
 * Shape-aware version of CornersFirst strategy. Uses the board shape's corner detection
 * instead of hardcoded hex corner logic, so it works with any board geometry.
 */
public class ShapeAwareCornersFirst implements IReversiStrategies {

  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> allMoves = StrategyUtils.getValidMoves(model, player);
    Set<Coordinate> corners = new HashSet<>(
            model.getBoardShape().getCorners(model.getBoardSize(), model.getBoard()));

    List<Coordinate> cornerMoves = new ArrayList<>();
    for (Coordinate move : allMoves) {
      if (corners.contains(move)) {
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
}
