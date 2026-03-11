package cs3500.reversi.network;

import java.util.List;

import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.Player;

/**
 * Stateless utility for encoding and decoding the Reversi network protocol messages.
 * All messages are single-line strings terminated by newline.
 */
public final class MessageParser {

  private MessageParser() {
    // utility class
  }

  // ---------------------------------------------------------------------------
  // Encode helpers (model/game data → protocol string)
  // ---------------------------------------------------------------------------

  /**
   * Encodes the full board state of a model as a STATE message payload.
   * Format: boardSize,currentTurn;row0c0,row0c1,...;row1c0,...
   * Cell encoding: _ = empty, B = BLACK, W = WHITE.
   */
  public static String encodeState(IReadOnlyReversiModel model) {
    StringBuilder sb = new StringBuilder();
    sb.append(model.getBoardSize());
    sb.append(",");
    sb.append(model.getCurrentTurn().name());
    List<List<cs3500.reversi.model.ISpace>> board = model.getBoard();
    for (int r = 0; r < board.size(); r++) {
      sb.append(";");
      for (int c = 0; c < model.getRow(r).size(); c++) {
        if (c > 0) {
          sb.append(",");
        }
        Player p = model.getSpaceContent(r, c);
        if (p == null) {
          sb.append("_");
        } else if (p == Player.BLACK) {
          sb.append("B");
        } else {
          sb.append("W");
        }
      }
    }
    return sb.toString();
  }

  /**
   * Decodes a STATE message payload into a {@link StateData} object.
   * @param payload the payload portion after "STATE:" prefix.
   * @return parsed state data.
   * @throws IllegalArgumentException if the payload is malformed.
   */
  public static StateData parseState(String payload) {
    String[] rows = payload.split(";", -1);
    if (rows.length < 2) {
      throw new IllegalArgumentException("Malformed STATE payload: " + payload);
    }
    // First segment: boardSize,currentTurn
    String[] header = rows[0].split(",", 2);
    int boardSize = Integer.parseInt(header[0]);
    Player currentTurn = Player.valueOf(header[1]);

    int totalRows = rows.length - 1;
    Player[][] board = new Player[totalRows][];
    for (int r = 0; r < totalRows; r++) {
      String[] cells = rows[r + 1].split(",", -1);
      board[r] = new Player[cells.length];
      for (int c = 0; c < cells.length; c++) {
        switch (cells[c]) {
          case "B":
            board[r][c] = Player.BLACK;
            break;
          case "W":
            board[r][c] = Player.WHITE;
            break;
          case "_":
            board[r][c] = null;
            break;
          default:
            throw new IllegalArgumentException("Unknown cell value: " + cells[c]);
        }
      }
    }
    return new StateData(boardSize, currentTurn, board);
  }

  // ---------------------------------------------------------------------------
  // Message construction helpers
  // ---------------------------------------------------------------------------

  /** Extracts the command name from a message (text before the first ':'). */
  public static String getCommand(String message) {
    int idx = message.indexOf(':');
    return idx < 0 ? message.trim() : message.substring(0, idx).trim();
  }

  /** Extracts the payload from a message (text after the first ':'). */
  public static String getPayload(String message) {
    int idx = message.indexOf(':');
    return idx < 0 ? "" : message.substring(idx + 1);
  }

  public static String welcomeMessage(Player color, int boardSize) {
    return "WELCOME:" + color.name() + "," + boardSize;
  }

  public static String stateMessage(IReadOnlyReversiModel model) {
    return "STATE:" + encodeState(model);
  }

  public static String moveMessage(int row, int col) {
    return "MOVE:" + row + "," + col;
  }

  public static String moveMadeMessage(Player player, int row, int col, int flippedCount) {
    return "MOVE_MADE:" + player.name() + "," + row + "," + col + "," + flippedCount;
  }

  public static String passMadeMessage(Player player) {
    return "PASS_MADE:" + player.name();
  }

  public static String gameOverMessage(int blackScore, int whiteScore) {
    return "GAME_OVER:" + blackScore + "," + whiteScore;
  }

  public static String invalidMessage(String reason) {
    return "INVALID:" + reason;
  }

  public static String undoDeniedMessage(String reason) {
    return "UNDO_DENIED:" + reason;
  }

  public static String errorMessage(String reason) {
    return "ERROR:" + reason;
  }

  // ---------------------------------------------------------------------------
  // Data holder for parsed state
  // ---------------------------------------------------------------------------

  /**
   * Immutable holder for parsed board state from a STATE message.
   */
  public static final class StateData {
    private final int boardSize;
    private final Player currentTurn;
    private final Player[][] board;

    StateData(int boardSize, Player currentTurn, Player[][] board) {
      this.boardSize = boardSize;
      this.currentTurn = currentTurn;
      this.board = board;
    }

    public int getBoardSize() {
      return boardSize;
    }

    public Player getCurrentTurn() {
      return currentTurn;
    }

    public Player[][] getBoard() {
      return board;
    }
  }
}
