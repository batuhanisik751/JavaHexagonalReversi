package cs3500.reversi.network;

import cs3500.reversi.model.Player;

/**
 * Callback interface for events received from the server by a {@link ReversiClient}.
 * All methods are called on the client's reader thread — implementations should
 * dispatch to the UI thread as needed.
 */
public interface ClientListener {

  /** Called when the server sends a full board state update. */
  void onStateUpdate(int boardSize, Player currentTurn, Player[][] boardState);

  /** Called when it is this client's turn to act. */
  void onYourTurn();

  /** Called when this client must wait for the opponent. */
  void onWait();

  /** Called when any player makes a move. */
  void onMoveMade(Player player, int row, int col, int flippedCount);

  /** Called when any player passes. */
  void onPassMade(Player player);

  /** Called when a requested undo was accepted. */
  void onUndoOk();

  /** Called when a requested undo was denied. */
  void onUndoDenied(String reason);

  /** Called when an attempted move was invalid. */
  void onInvalidMove(String reason);

  /** Called when the game is over. */
  void onGameOver(int blackScore, int whiteScore);

  /** Called when the opponent disconnects or times out. */
  void onOpponentDisconnected();

  /** Called on any connection error (broken pipe, server crash, etc.). */
  void onConnectionError(String message);
}
