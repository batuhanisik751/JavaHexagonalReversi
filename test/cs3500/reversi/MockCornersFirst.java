package cs3500.reversi;

import java.util.ArrayList;
import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.strategy.IReversiStrategies;

/**
 * Represents a Mock of the CornersFirst IReversiStrategies used for testing.
 */
public class MockCornersFirst implements IReversiStrategies {

  StringBuilder log;

  MockCornersFirst(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> validMoves = new ArrayList<>();
    IReversiModel copyModel = model.copyModel();
    for (int row = 0; row < copyModel.getBoard().size(); row++) {
      for (int col = 0; col < copyModel.getRow(row).size(); col++) {
        log.append("testing move : ")
                .append("row = ").append(row).append(", ")
                .append("col = ").append(col).append(" \n");
        if (copyModel.isValidMove(row, col, player) && !isCorner(row, col, copyModel)) {
          log.append("found valid (non-corner) move : ")
                  .append("row = ").append(row).append(", ")
                  .append("col = ").append(col).append(" \n");
        }
        if (copyModel.isValidMove(row, col, player) && isCorner(row, col, copyModel)) {
          log.append("found valid (corner) move : ")
                  .append("row = ").append(row).append(", ")
                  .append("col = ").append(col).append(" \n");
          validMoves.add(new Coordinate(row, col));
        }
      }
    }
    if (!validMoves.isEmpty()) {
      Coordinate selectedMove = validMoves.get(0);
      log.append("Best move found : ")
              .append("row = ").append(selectedMove.getRow())
              .append(", col = ").append(selectedMove.getCol());
      copyModel.move(selectedMove.getRow(), selectedMove.getCol(), player);
    } else {
      log.append("No valid moves at corners. Passing turn.");
      copyModel.passTurn();
    }
  }

  /**
   * Checks if the given Space is a corner Space.
   * @param row the row of the Space
   * @param col column of the Space
   * @param model the IReversiModel that is being looked at.
   * @return true if the Space is at a corner, false if not.
   */
  private boolean isCorner(int row, int col, IReversiModel model) {
    // checks top and bottom row
    if (row == 0 || row == model.getBoard().size() - 1) {
      return (col == 0 || col == model.getRow(row).size() - 1);
      // checks the middle row
    } else if (row == model.getBoardSize() - 1) {
      return (col == 0 || col == model.getRow(row).size() - 1);
    }
    return false;
  }
}
