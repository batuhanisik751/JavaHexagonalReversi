package cs3500.reversi;

import java.util.ArrayList;
import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.strategy.IReversiStrategies;

/**
 * Represents a mock of the AvoidMovesNextToCorners IReversiStrategies used for testing.
 */
public class MockAvoidCorners implements IReversiStrategies {
  StringBuilder log;

  MockAvoidCorners(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> validMoves = new ArrayList<>();
    IReversiModel copyModel = model.copyModel();
    for (int row = 0; row < model.getBoard().size(); row++) {
      for (int col = 0; col < model.getRow(row).size(); col++) {
        log.append("testing move : ")
                .append("row = ").append(row).append(", ")
                .append("col = ").append(col).append(" \n");
        if (copyModel.isValidMove(row, col, player) && isNextToCorner(row, col, copyModel)) {
          log.append("found valid move next to corner : ")
                  .append("row = ").append(row).append(", ")
                  .append("col = ").append(col).append(" \n");
        }
        if (copyModel.isValidMove(row, col, player) && !isNextToCorner(row, col, copyModel)) {
          log.append("found valid move not next to corner : ")
                  .append("row = ").append(row).append(", ")
                  .append("col = ").append(col).append(" \n");
          validMoves.add(new Coordinate(row, col));
        }
      }
    }
    if (!validMoves.isEmpty()) {
      Coordinate selectedMove = validMoves.get(0);
      copyModel.move(selectedMove.getRow(), selectedMove.getCol(), player);
      log.append("Best move found : ")
              .append("row = ").append(selectedMove.getRow())
              .append(", col = ").append(selectedMove.getCol());
    }
    else {
      log.append("No valid moves not next to corners. Passing turn.");
    }
  }

  /**
   * Checks if a given Space is next to a corner.
   * @param row the row of the Space.
   * @param col column of the Space.
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
      // checks middle row corners
    } else if (row == model.getBoardSize() - 1) {
      return (col == 1 || col == model.getRow(row).size() - 2);
      // checks rows around middle row
    } else if (row == model.getBoardSize() || row == model.getBoardSize() - 2) {
      return (col == 0 || col == model.getRow(row).size() - 1);
    }
    return false;
  }
}
