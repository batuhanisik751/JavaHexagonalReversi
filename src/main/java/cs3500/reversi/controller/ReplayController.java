package cs3500.reversi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;

/**
 * Manages game replay logic. Precomputes all board snapshots so that
 * stepping forward and back is instant. No JavaFX dependency — pure logic.
 */
public class ReplayController {
  private final List<MoveRecord> moves;
  private final List<IReversiModel> snapshots;
  private final List<List<Coordinate>> flippedPerStep;
  private final IReversiModel model;
  private int currentStep;

  /**
   * Constructs a ReplayController by replaying all moves on a fresh model.
   * @param boardSize the board size for the game.
   * @param moves the list of move records to replay.
   */
  public ReplayController(int boardSize, List<MoveRecord> moves) {
    this.moves = new ArrayList<>(moves);
    this.snapshots = new ArrayList<>();
    this.flippedPerStep = new ArrayList<>();
    this.model = new ReversiModel(boardSize);
    this.currentStep = 0;

    buildSnapshots(boardSize);
  }

  private void buildSnapshots(int boardSize) {
    IReversiModel builder = new ReversiModel(boardSize);
    // Snapshot 0 = initial state
    snapshots.add(builder.copyModel());
    flippedPerStep.add(Collections.emptyList());

    for (MoveRecord record : moves) {
      List<Coordinate> flipped;
      if (record.isPass()) {
        builder.passTurn();
        flipped = Collections.emptyList();
      } else {
        Player[][] before = snapshotBoard(builder);
        builder.move(record.getRow(), record.getCol(), record.getPlayer());
        flipped = computeFlipped(builder, before, record.getRow(), record.getCol());
      }
      snapshots.add(builder.copyModel());
      flippedPerStep.add(flipped);
    }

    // Set model to initial state
    model.restoreFrom(snapshots.get(0));
  }

  private Player[][] snapshotBoard(IReadOnlyReversiModel m) {
    int rows = m.getBoard().size();
    Player[][] snapshot = new Player[rows][];
    for (int r = 0; r < rows; r++) {
      int cols = m.getRow(r).size();
      snapshot[r] = new Player[cols];
      for (int c = 0; c < cols; c++) {
        snapshot[r][c] = m.getSpaceContent(r, c);
      }
    }
    return snapshot;
  }

  private List<Coordinate> computeFlipped(IReadOnlyReversiModel after,
                                          Player[][] before, int moveRow, int moveCol) {
    List<Coordinate> flipped = new ArrayList<>();
    for (int r = 0; r < after.getBoard().size(); r++) {
      for (int c = 0; c < after.getRow(r).size(); c++) {
        if (r == moveRow && c == moveCol) {
          continue;
        }
        Player afterPlayer = after.getSpaceContent(r, c);
        if (afterPlayer != null && afterPlayer != before[r][c]) {
          flipped.add(new Coordinate(r, c));
        }
      }
    }
    return Collections.unmodifiableList(flipped);
  }

  /**
   * Steps forward one move. Returns false if already at the end.
   */
  public boolean stepForward() {
    if (currentStep >= moves.size()) {
      return false;
    }
    currentStep++;
    model.restoreFrom(snapshots.get(currentStep));
    return true;
  }

  /**
   * Steps back one move. Returns false if already at the start.
   */
  public boolean stepBack() {
    if (currentStep <= 0) {
      return false;
    }
    currentStep--;
    model.restoreFrom(snapshots.get(currentStep));
    return true;
  }

  /**
   * Jumps to the initial board state (before any moves).
   */
  public void goToStart() {
    currentStep = 0;
    model.restoreFrom(snapshots.get(0));
  }

  /**
   * Jumps to the final board state (after all moves).
   */
  public void goToEnd() {
    currentStep = moves.size();
    model.restoreFrom(snapshots.get(currentStep));
  }

  /**
   * Returns the current step index (0 = initial state, n = after move n).
   */
  public int getCurrentStep() {
    return currentStep;
  }

  /**
   * Returns the total number of moves in the replay.
   */
  public int getTotalSteps() {
    return moves.size();
  }

  /**
   * Returns the MoveRecord for the current step, or null if at step 0.
   */
  public MoveRecord getCurrentRecord() {
    if (currentStep == 0) {
      return null;
    }
    return moves.get(currentStep - 1);
  }

  /**
   * Returns the list of flipped coordinates for the current step.
   * Empty if at step 0 or if the current step is a pass.
   */
  public List<Coordinate> getCurrentFlipped() {
    return flippedPerStep.get(currentStep);
  }

  /**
   * Returns the live model at the current step (read-only access).
   */
  public IReadOnlyReversiModel getModel() {
    return model;
  }

  /**
   * Returns the history records up to and including the current step.
   */
  public List<MoveRecord> getHistoryUpToCurrent() {
    if (currentStep == 0) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(moves.subList(0, currentStep));
  }
}
