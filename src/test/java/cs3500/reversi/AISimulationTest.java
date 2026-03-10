package cs3500.reversi;

import org.junit.Test;

import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.strategy.AsManyPiecesAsPossible;
import cs3500.reversi.strategy.AvoidNextToCorners;
import cs3500.reversi.strategy.CornersFirst;
import cs3500.reversi.strategy.AlphaBetaMiniMax;
import cs3500.reversi.strategy.IReversiStrategies;
import cs3500.reversi.strategy.MiniMax;
import cs3500.reversi.strategy.StrategyUtils;

import static org.junit.Assert.assertTrue;

/**
 * Headless AI vs AI simulation to diagnose stalling issues.
 * Runs games without GUI, logging every turn in detail.
 */
public class AISimulationTest {

  /**
   * Runs a single AI vs AI game and returns a detailed log.
   * Returns true if the game completed normally, false if it stalled.
   */
  private boolean runGame(String label, IReversiStrategies blackStrategy,
                          IReversiStrategies whiteStrategy, int boardSize, StringBuilder log) {
    IReversiModel model = new ReversiModel(boardSize);
    int maxTurns = 200;
    int consecutivePasses = 0;
    int turnNumber = 0;

    log.append("\n========== ").append(label).append(" ==========\n");
    log.append("Board size: ").append(boardSize).append("\n\n");

    while (!model.gameOver() && turnNumber < maxTurns) {
      turnNumber++;
      Player current = model.getCurrentTurn();
      IReversiStrategies strategy = (current == Player.BLACK) ? blackStrategy : whiteStrategy;
      String stratName = (current == Player.BLACK) ? "BLACK" : "WHITE";

      // Count valid moves before the strategy plays
      List<Coordinate> validMoves = StrategyUtils.getValidMoves(model, current);
      int scoreBlack = model.getScore(Player.BLACK);
      int scoreWhite = model.getScore(Player.WHITE);

      log.append(String.format("Turn %3d | %s | Valid moves: %d | Score B:%d W:%d",
              turnNumber, stratName, validMoves.size(), scoreBlack, scoreWhite));

      if (validMoves.size() > 0) {
        log.append(" | Options: ");
        for (int i = 0; i < Math.min(validMoves.size(), 8); i++) {
          Coordinate c = validMoves.get(i);
          log.append(String.format("(%d,%d) ", c.getRow(), c.getCol()));
        }
        if (validMoves.size() > 8) {
          log.append("...");
        }
      }

      // Snapshot turn before strategy plays
      Player turnBefore = model.getCurrentTurn();
      IReversiModel snapshot = model.copyModel();

      // Run the strategy
      boolean strategyThrew = false;
      try {
        strategy.chooseNextMove(model, current);
      } catch (Exception ex) {
        log.append(" | EXCEPTION: ").append(ex.getClass().getSimpleName())
                .append(": ").append(ex.getMessage());
        strategyThrew = true;
      }

      // Analyze what happened
      Player turnAfter = model.getCurrentTurn();
      boolean turnChanged = (turnBefore != turnAfter);

      if (strategyThrew) {
        log.append(" | STRATEGY THREW — turn did NOT change, AI is STUCK\n");
        // The turn didn't change, so on next iteration same player plays again
        // This would cause infinite loop — break
        log.append(">>> STALL DETECTED: Exception broke the AI. Game cannot continue.\n");
        return false;
      }

      if (!turnChanged) {
        log.append(" | BUG: Turn did NOT change after strategy played!\n");
        log.append(">>> STALL DETECTED: Turn stuck on ").append(stratName).append("\n");
        return false;
      }

      // Determine if the strategy moved or passed
      boolean passed = true;
      int moveRow = -1;
      int moveCol = -1;
      for (int r = 0; r < model.getBoard().size(); r++) {
        for (int c = 0; c < model.getRow(r).size(); c++) {
          Player before = snapshot.getSpaceContent(r, c);
          Player after = model.getSpaceContent(r, c);
          if (before == null && after == current) {
            // This is the newly placed piece
            if (moveRow == -1) {
              moveRow = r;
              moveCol = c;
              passed = false;
            }
          }
        }
      }

      if (passed) {
        if (validMoves.size() > 0) {
          log.append(" | PASSED despite having ").append(validMoves.size())
                  .append(" valid moves! BUG!\n");
          log.append(">>> STALL RISK: AI passed when it had moves. ");
          log.append("Opponent valid moves: ")
                  .append(StrategyUtils.getValidMoves(model, model.getCurrentTurn()).size())
                  .append("\n");
          consecutivePasses++;
        } else {
          log.append(" | Passed (no valid moves)\n");
          consecutivePasses++;
        }
      } else {
        boolean wasValid = false;
        for (Coordinate v : validMoves) {
          if (v.getRow() == moveRow && v.getCol() == moveCol) {
            wasValid = true;
            break;
          }
        }
        log.append(String.format(" | Moved to (%d,%d) valid=%b\n", moveRow, moveCol, wasValid));
        consecutivePasses = 0;
      }

      if (consecutivePasses >= 2) {
        log.append(">>> Two consecutive passes detected — game should end via gameOver().\n");
        if (!model.gameOver()) {
          log.append(">>> BUG: gameOver() returned false after two consecutive passes!\n");
        }
      }
    }

    if (model.gameOver()) {
      int bScore = model.getScore(Player.BLACK);
      int wScore = model.getScore(Player.WHITE);
      String winner = bScore > wScore ? "BLACK" : (wScore > bScore ? "WHITE" : "TIE");
      log.append(String.format("\nGame COMPLETED in %d turns. Final: B:%d W:%d Winner: %s\n",
              turnNumber, bScore, wScore, winner));
      return true;
    } else {
      log.append(String.format(
              "\n>>> STALL: Game did NOT finish after %d turns. Stuck on %s's turn.\n",
              maxTurns, model.getCurrentTurn()));
      return false;
    }
  }

  @Test
  public void simulateMultipleGames() {
    IReversiStrategies[] strategies = {
            new AsManyPiecesAsPossible(),
            new CornersFirst(),
            new AvoidNextToCorners(),
            new MiniMax(),
            new AlphaBetaMiniMax(3)
    };
    String[] names = {"AsManyPieces", "CornersFirst", "AvoidNextToCorners", "MiniMax", "AlphaBeta"};

    StringBuilder fullLog = new StringBuilder();
    int totalGames = 0;
    int completedGames = 0;
    int stalledGames = 0;

    // Run every strategy combination on board sizes 3 through 6
    for (int boardSize = 3; boardSize <= 6; boardSize++) {
      for (int b = 0; b < strategies.length; b++) {
        for (int w = 0; w < strategies.length; w++) {
          totalGames++;
          String label = String.format("Game %d: %s(B) vs %s(W) [board=%d]",
                  totalGames, names[b], names[w], boardSize);
          boolean completed = runGame(label, strategies[b], strategies[w], boardSize, fullLog);
          if (completed) {
            completedGames++;
          } else {
            stalledGames++;
          }
        }
      }
    }

    // Print summary
    fullLog.append("\n\n==================== SUMMARY ====================\n");
    fullLog.append(String.format("Total games: %d | Completed: %d | Stalled: %d\n",
            totalGames, completedGames, stalledGames));

    System.out.println(fullLog.toString());

    // The test passes for diagnostic purposes — we want to see the output
    assertTrue("All games should complete. Stalled: " + stalledGames,
            stalledGames == 0);
  }
}
