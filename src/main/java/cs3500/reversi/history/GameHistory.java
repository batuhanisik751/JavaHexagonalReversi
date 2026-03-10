package cs3500.reversi.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.Player;

/**
 * Records the history of moves and passes in a Reversi game.
 * Shared between controllers so both players see the same log.
 */
public final class GameHistory {
  private final List<MoveRecord> records = new ArrayList<>();

  /**
   * Records a move (piece placement with flips).
   */
  public void recordMove(Player player, int row, int col, List<Coordinate> flipped) {
    records.add(MoveRecord.move(records.size() + 1, player, row, col, flipped));
  }

  /**
   * Records a pass.
   */
  public void recordPass(Player player) {
    records.add(MoveRecord.pass(records.size() + 1, player));
  }

  /**
   * Removes the last record. No-op if history is empty.
   */
  public void undoLast() {
    if (!records.isEmpty()) {
      records.remove(records.size() - 1);
    }
  }

  /**
   * Clears all records.
   */
  public void clear() {
    records.clear();
  }

  /**
   * Returns an unmodifiable copy of the recorded moves.
   */
  public List<MoveRecord> getRecords() {
    return Collections.unmodifiableList(new ArrayList<>(records));
  }
}
