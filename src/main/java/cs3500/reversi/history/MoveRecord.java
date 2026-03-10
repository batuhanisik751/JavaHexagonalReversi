package cs3500.reversi.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.Player;

/**
 * Immutable record of a single game action (move or pass).
 */
public final class MoveRecord {
  private final int moveNumber;
  private final Player player;
  private final int row;
  private final int col;
  private final List<Coordinate> flipped;
  private final boolean isPass;

  private MoveRecord(int moveNumber, Player player, int row, int col,
                     List<Coordinate> flipped, boolean isPass) {
    this.moveNumber = moveNumber;
    this.player = player;
    this.row = row;
    this.col = col;
    this.flipped = Collections.unmodifiableList(new ArrayList<>(flipped));
    this.isPass = isPass;
  }

  /**
   * Creates a move record for a piece placement.
   */
  public static MoveRecord move(int moveNumber, Player player, int row, int col,
                                List<Coordinate> flipped) {
    return new MoveRecord(moveNumber, player, row, col, flipped, false);
  }

  /**
   * Creates a move record for a pass.
   */
  public static MoveRecord pass(int moveNumber, Player player) {
    return new MoveRecord(moveNumber, player, -1, -1, new ArrayList<>(), true);
  }

  public int getMoveNumber() {
    return moveNumber;
  }

  public Player getPlayer() {
    return player;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  public List<Coordinate> getFlipped() {
    return flipped;
  }

  public boolean isPass() {
    return isPass;
  }

  /**
   * Returns a display-friendly string for this record.
   * Move:  "1. X -> (2, 3) flipped 2"
   * Pass:  "2. O passed"
   */
  public String toDisplayString() {
    if (isPass) {
      return moveNumber + ". " + player + " passed";
    }
    return moveNumber + ". " + player + " -> (" + row + ", " + col + ") flipped " + flipped.size();
  }
}
