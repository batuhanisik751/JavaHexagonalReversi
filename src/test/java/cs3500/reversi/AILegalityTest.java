package cs3500.reversi;

import org.junit.Test;

import java.util.ArrayList;
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

import static org.junit.Assert.assertEquals;

/**
 * Validates that AI strategies never make illegal moves across all combinations
 * and board sizes. Checks move legality, flip correctness, score consistency,
 * and turn integrity on every single turn.
 */
public class AILegalityTest {

  private static class Violation {
    final String game;
    final int turn;
    final String description;

    Violation(String game, int turn, String description) {
      this.game = game;
      this.turn = turn;
      this.description = description;
    }

    @Override
    public String toString() {
      return String.format("[%s turn %d] %s", game, turn, description);
    }
  }

  private List<Violation> runGame(String label, IReversiStrategies blackStrat,
                                   IReversiStrategies whiteStrat, int boardSize) {
    List<Violation> violations = new ArrayList<>();
    IReversiModel model = new ReversiModel(boardSize);
    int maxTurns = 300;
    int turnNumber = 0;

    while (!model.gameOver() && turnNumber < maxTurns) {
      turnNumber++;
      Player current = model.getCurrentTurn();
      IReversiStrategies strategy = (current == Player.BLACK) ? blackStrat : whiteStrat;

      // --- Pre-move checks ---
      List<Coordinate> validMovesBefore = StrategyUtils.getValidMoves(model, current);
      Player opponentPlayer = (current == Player.BLACK) ? Player.WHITE : Player.BLACK;
      List<Coordinate> opponentMovesBefore = StrategyUtils.getValidMoves(model, opponentPlayer);
      int scoreBBefore = model.getScore(Player.BLACK);
      int scoreWBefore = model.getScore(Player.WHITE);
      int totalPiecesBefore = scoreBBefore + scoreWBefore;
      Player turnBefore = model.getCurrentTurn();

      // Snapshot every cell
      int rows = model.getBoard().size();
      Player[][] boardBefore = new Player[rows][];
      for (int r = 0; r < rows; r++) {
        int cols = model.getRow(r).size();
        boardBefore[r] = new Player[cols];
        for (int c = 0; c < cols; c++) {
          boardBefore[r][c] = model.getSpaceContent(r, c);
        }
      }

      // --- Execute strategy ---
      boolean threw = false;
      try {
        strategy.chooseNextMove(model, current);
      } catch (Exception ex) {
        violations.add(new Violation(label, turnNumber,
                "Strategy threw: " + ex.getClass().getSimpleName() + ": " + ex.getMessage()));
        threw = true;
      }

      if (threw) {
        break;
      }

      // --- Post-move checks ---
      Player turnAfter = model.getCurrentTurn();
      int scoreBAfter = model.getScore(Player.BLACK);
      int scoreWAfter = model.getScore(Player.WHITE);
      int totalPiecesAfter = scoreBAfter + scoreWAfter;

      // Check 1: Turn must have changed
      if (turnBefore == turnAfter) {
        violations.add(new Violation(label, turnNumber,
                "Turn did not change after strategy played. Stuck on " + turnBefore));
        break;
      }

      // Detect placed piece and flipped pieces
      Coordinate placedAt = null;
      List<Coordinate> flippedCells = new ArrayList<>();
      List<Coordinate> newlyEmpty = new ArrayList<>();
      List<Coordinate> unexpectedChanges = new ArrayList<>();

      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < model.getRow(r).size(); c++) {
          Player before = boardBefore[r][c];
          Player after = model.getSpaceContent(r, c);
          if (before == null && after != null) {
            if (after == current) {
              if (placedAt != null) {
                violations.add(new Violation(label, turnNumber,
                        String.format("Multiple pieces placed: (%d,%d) and (%d,%d)",
                                placedAt.getRow(), placedAt.getCol(), r, c)));
              }
              placedAt = new Coordinate(r, c);
            } else {
              violations.add(new Violation(label, turnNumber,
                      String.format("Piece appeared for WRONG player at (%d,%d): "
                              + "expected %s got %s", r, c, current, after)));
            }
          } else if (before != null && after == null) {
            newlyEmpty.add(new Coordinate(r, c));
          } else if (before != null && after != null && before != after) {
            flippedCells.add(new Coordinate(r, c));
          }
        }
      }

      boolean passed = (placedAt == null && flippedCells.isEmpty() && newlyEmpty.isEmpty());

      // Check 2: If passed, must have no valid moves
      if (passed && validMovesBefore.size() > 0) {
        violations.add(new Violation(label, turnNumber,
                "PASSED with " + validMovesBefore.size() + " valid moves available: "
                        + coordsToString(validMovesBefore)));
      }

      // Check 3: If moved, the placed cell must be in validMovesBefore
      if (placedAt != null) {
        boolean wasValid = false;
        for (Coordinate v : validMovesBefore) {
          if (v.getRow() == placedAt.getRow() && v.getCol() == placedAt.getCol()) {
            wasValid = true;
            break;
          }
        }
        if (!wasValid) {
          violations.add(new Violation(label, turnNumber,
                  String.format("ILLEGAL MOVE at (%d,%d) — not in valid moves list: %s",
                          placedAt.getRow(), placedAt.getCol(),
                          coordsToString(validMovesBefore))));
        }
      }

      // Check 4: No pieces should vanish
      if (!newlyEmpty.isEmpty()) {
        violations.add(new Violation(label, turnNumber,
                "Pieces VANISHED at: " + coordsToString(newlyEmpty)));
      }

      // Check 5: Flipped pieces must be opponent's
      for (Coordinate f : flippedCells) {
        Player wasBefore = boardBefore[f.getRow()][f.getCol()];
        Player isNow = model.getSpaceContent(f.getRow(), f.getCol());
        if (wasBefore != opponentPlayer || isNow != current) {
          violations.add(new Violation(label, turnNumber,
                  String.format("Bad flip at (%d,%d): was %s now %s (expected %s->%s)",
                          f.getRow(), f.getCol(), wasBefore, isNow, opponentPlayer, current)));
        }
      }

      // Check 6: If moved, total pieces must increase by exactly 1
      if (!passed && placedAt != null) {
        if (totalPiecesAfter != totalPiecesBefore + 1) {
          violations.add(new Violation(label, turnNumber,
                  String.format("Piece count wrong: before=%d after=%d (expected +1)",
                          totalPiecesBefore, totalPiecesAfter)));
        }
      }

      // Check 7: If passed, board must be identical
      if (passed) {
        if (totalPiecesAfter != totalPiecesBefore) {
          violations.add(new Violation(label, turnNumber,
                  "Board changed during pass: pieces before=" + totalPiecesBefore
                          + " after=" + totalPiecesAfter));
        }
      }

      // Check 8: Score consistency (B + W = total pieces on board)
      int actualPieces = 0;
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < model.getRow(r).size(); c++) {
          if (model.getSpaceContent(r, c) != null) {
            actualPieces++;
          }
        }
      }
      if (actualPieces != scoreBAfter + scoreWAfter) {
        violations.add(new Violation(label, turnNumber,
                String.format("Score mismatch: counted %d pieces but scores sum to %d (B:%d W:%d)",
                        actualPieces, scoreBAfter + scoreWAfter, scoreBAfter, scoreWAfter)));
      }

      // Check 9: If moved, at least one piece must have flipped
      if (placedAt != null && flippedCells.isEmpty()) {
        violations.add(new Violation(label, turnNumber,
                String.format("Move at (%d,%d) flipped ZERO pieces — illegal in Reversi",
                        placedAt.getRow(), placedAt.getCol())));
      }

      // Check 10: getCurrentTurn must not have been corrupted by gameOver() call
      Player turnCheck1 = model.getCurrentTurn();
      model.gameOver();
      Player turnCheck2 = model.getCurrentTurn();
      if (turnCheck1 != turnCheck2) {
        violations.add(new Violation(label, turnNumber,
                "gameOver() CORRUPTED currentTurn: was " + turnCheck1 + " now " + turnCheck2));
      }
    }

    if (turnNumber >= maxTurns && !model.gameOver()) {
      violations.add(new Violation(label, turnNumber, "Game did not finish within " + maxTurns + " turns"));
    }

    return violations;
  }

  private String coordsToString(List<Coordinate> coords) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < Math.min(coords.size(), 10); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(String.format("(%d,%d)", coords.get(i).getRow(), coords.get(i).getCol()));
    }
    if (coords.size() > 10) {
      sb.append(", ...(").append(coords.size()).append(" total)");
    }
    sb.append("]");
    return sb.toString();
  }

  @Test
  public void validateAllAIMovesAreLegal() {
    IReversiStrategies[] strategies = {
            new AsManyPiecesAsPossible(),
            new CornersFirst(),
            new AvoidNextToCorners(),
            new MiniMax(),
            new AlphaBetaMiniMax(3)
    };
    String[] names = {"AsManyPieces", "CornersFirst", "AvoidCorners", "MiniMax", "AlphaBeta"};

    List<Violation> allViolations = new ArrayList<>();
    int totalGames = 0;
    int totalTurns = 0;

    for (int boardSize = 3; boardSize <= 7; boardSize++) {
      for (int b = 0; b < strategies.length; b++) {
        for (int w = 0; w < strategies.length; w++) {
          totalGames++;
          String label = String.format("%s_vs_%s_board%d", names[b], names[w], boardSize);
          List<Violation> v = runGame(label, strategies[b], strategies[w], boardSize);
          allViolations.addAll(v);
        }
      }
    }

    // Print report
    StringBuilder report = new StringBuilder();
    report.append("\n============= AI LEGALITY REPORT =============\n");
    report.append(String.format("Games played: %d\n", totalGames));
    report.append(String.format("Board sizes: 3-7\n"));
    report.append(String.format("Strategy combos: %d per board size\n", strategies.length * strategies.length));
    report.append(String.format("Violations found: %d\n\n", allViolations.size()));

    if (!allViolations.isEmpty()) {
      report.append("--- VIOLATIONS ---\n");
      for (Violation v : allViolations) {
        report.append(v.toString()).append("\n");
      }
    } else {
      report.append("ALL MOVES LEGAL. No violations detected.\n");
    }

    System.out.println(report.toString());
    assertEquals("Expected zero violations:\n" + report.toString(), 0, allViolations.size());
  }
}
