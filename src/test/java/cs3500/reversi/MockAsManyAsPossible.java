package cs3500.reversi;

import java.util.ArrayList;
import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.strategy.IReversiStrategies;

/**
 * Represents a mock of the AsManyAsPossible IReversiStrategies used for testing.
 */
public class MockAsManyAsPossible implements IReversiStrategies {
  StringBuilder log;

  MockAsManyAsPossible(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> validMoves = new ArrayList<>();
    int highestScore = -1;
    Coordinate bestMove = null;
    IReversiModel copyModel = model.copyModel();
    for (int row = 0; row < model.getBoard().size(); row++) {
      for (int col = 0; col < model.getRow(row).size(); col++) {
        log.append("testing move : ")
                .append("row = ").append(row).append(", ")
                .append("col = ").append(col).append(" \n");
        if (model.isValidMove(row, col, player)) {
          validMoves.add(new Coordinate(row, col));
        }
      }
    }
    if (!validMoves.isEmpty()) {
      for (Coordinate move : validMoves) {
        IReversiModel copyOfCopy = copyModel.copyModel();
        copyOfCopy.move(move.getRow(), move.getCol(), copyOfCopy.getCurrentTurn());
        log.append("found valid move : ")
                .append("row = ").append(move.getRow()).append(", ")
                .append("col = ").append(move.getCol()).append(", ")
                .append("score = ")
                .append(copyOfCopy.getOpponentScore(copyOfCopy.getCurrentTurn())).append(" \n");
        if (copyOfCopy.getOpponentScore(copyOfCopy.getCurrentTurn()) > highestScore) {
          highestScore = copyOfCopy.getOpponentScore(copyOfCopy.getCurrentTurn());
          bestMove = move;
        }
      }
      log.append("Best move : row = ").append(bestMove.getRow())
              .append(", col = ").append(bestMove.getCol());
    } else {
      log.append("No valid moves found. Passing turn.");
    }
  }
}
