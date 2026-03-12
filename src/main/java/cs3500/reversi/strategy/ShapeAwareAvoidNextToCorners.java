package cs3500.reversi.strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IBoardShape;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

/**
 * Shape-aware version of AvoidNextToCorners strategy. Uses the board shape's corner
 * and adjacency detection instead of hardcoded hex logic.
 */
public class ShapeAwareAvoidNextToCorners implements IReversiStrategies {

  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> allMoves = StrategyUtils.getValidMoves(model, player);
    Set<Coordinate> adjacentToCorners = computeAdjacentToCorners(model);

    List<Coordinate> filteredMoves = new ArrayList<>();
    for (Coordinate move : allMoves) {
      if (!adjacentToCorners.contains(move)) {
        filteredMoves.add(move);
      }
    }

    Coordinate bestMove;
    if (!filteredMoves.isEmpty()) {
      bestMove = filteredMoves.get(0);
    } else if (!allMoves.isEmpty()) {
      bestMove = allMoves.get(0);
    } else {
      bestMove = null;
    }
    StrategyUtils.executeOrPass(model, bestMove, player);
  }

  private Set<Coordinate> computeAdjacentToCorners(IReversiModel model) {
    IBoardShape shape = model.getBoardShape();
    List<Coordinate> corners = shape.getCorners(model.getBoardSize(), model.getBoard());
    Set<Coordinate> adjacent = new HashSet<>();

    for (Coordinate corner : corners) {
      for (int[] dir : shape.getDirections(corner.getRow(), corner.getCol())) {
        int r = corner.getRow() + dir[0];
        int c = corner.getCol() + dir[1];
        if (shape.isWithinBounds(model.getBoard(), r, c)) {
          adjacent.add(new Coordinate(r, c));
        }
      }
    }
    return adjacent;
  }
}
