package cs3500.reversi;

import java.util.ArrayList;
import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.strategy.AsManyPiecesAsPossible;
import cs3500.reversi.strategy.IReversiStrategies;

/**
 * Represents a Mock of the MiniMax IReversiStrategies used for testing.
 */
public class MockMiniMax implements IReversiStrategies {
  StringBuilder log = new StringBuilder();

  MockMiniMax(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void chooseNextMove(IReversiModel model, Player player) {
    List<Coordinate> validMoves = new ArrayList<>();
    int lowestScore = 999;
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
        IReversiStrategies maxOpponentScore = new AsManyPiecesAsPossible();
        copyOfCopy.move(move.getRow(), move.getCol(), copyOfCopy.getCurrentTurn());
        maxOpponentScore.chooseNextMove(copyOfCopy, copyOfCopy.getCurrentTurn());
        log.append("found valid move : ")
                .append("row = ").append(move.getRow()).append(", ")
                .append("col = ").append(move.getCol()).append(", ")
                .append("max opponent score = ")
                .append(copyOfCopy.getOpponentScore(copyOfCopy.getCurrentTurn())).append(" \n");
        if (copyOfCopy.getOpponentScore(copyOfCopy.getCurrentTurn()) < lowestScore) {
          lowestScore = copyOfCopy.getOpponentScore(copyOfCopy.getCurrentTurn());
          bestMove = move;
        }
      }
      log.append("Best move : row = ").append(bestMove.getRow())
              .append(", col = ").append(bestMove.getCol());
    }
    else {
      log.append("No valid moves found. Passing turn.");
    }
  }
}
