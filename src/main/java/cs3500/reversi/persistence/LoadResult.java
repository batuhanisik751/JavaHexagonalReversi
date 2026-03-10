package cs3500.reversi.persistence;

import java.util.List;

import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.Player;

/**
 * Holds the parsed data from a loaded .reversi save file.
 */
public final class LoadResult {
  private final int boardSize;
  private final Player currentTurn;
  private final Player[][] boardState;
  private final List<MoveRecord> history;

  LoadResult(int boardSize, Player currentTurn, Player[][] boardState, List<MoveRecord> history) {
    this.boardSize = boardSize;
    this.currentTurn = currentTurn;
    this.boardState = boardState;
    this.history = history;
  }

  public int getBoardSize() {
    return boardSize;
  }

  public Player getCurrentTurn() {
    return currentTurn;
  }

  public Player[][] getBoardState() {
    return boardState;
  }

  public List<MoveRecord> getHistory() {
    return history;
  }
}
