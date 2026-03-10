package cs3500.reversi.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.Player;

/**
 * Deserializes a Reversi game state and history from a plain-text .reversi file.
 */
public final class GameLoader {

  /**
   * Loads a game state from the given file.
   * @param file the file to read from.
   * @return a LoadResult containing the parsed game state.
   * @throws IOException if reading or parsing fails.
   */
  public static LoadResult load(File file) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String boardSizeLine = reader.readLine();
      if (boardSizeLine == null || !boardSizeLine.startsWith("BOARD_SIZE:")) {
        throw new IOException("Invalid save file: missing BOARD_SIZE.");
      }
      int boardSize = Integer.parseInt(boardSizeLine.substring("BOARD_SIZE:".length()));

      String turnLine = reader.readLine();
      if (turnLine == null || !turnLine.startsWith("CURRENT_TURN:")) {
        throw new IOException("Invalid save file: missing CURRENT_TURN.");
      }
      Player currentTurn = Player.valueOf(turnLine.substring("CURRENT_TURN:".length()));

      String boardHeader = reader.readLine();
      if (boardHeader == null || !boardHeader.equals("BOARD:")) {
        throw new IOException("Invalid save file: missing BOARD header.");
      }

      int totalRows = (boardSize * 2) - 1;
      Player[][] boardState = new Player[totalRows][];
      for (int r = 0; r < totalRows; r++) {
        String rowLine = reader.readLine();
        if (rowLine == null) {
          throw new IOException("Invalid save file: missing board row " + r + ".");
        }
        String[] cells = rowLine.split(",");
        boardState[r] = new Player[cells.length];
        for (int c = 0; c < cells.length; c++) {
          switch (cells[c]) {
            case "B":
              boardState[r][c] = Player.BLACK;
              break;
            case "W":
              boardState[r][c] = Player.WHITE;
              break;
            case "_":
              boardState[r][c] = null;
              break;
            default:
              throw new IOException("Invalid cell value: " + cells[c]);
          }
        }
      }

      List<MoveRecord> history = new ArrayList<>();
      String historyHeader = reader.readLine();
      if (historyHeader != null && historyHeader.equals("HISTORY:")) {
        String line;
        int moveNum = 1;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
          String[] parts = line.split(",");
          if (parts[0].equals("PASS")) {
            history.add(MoveRecord.pass(moveNum, Player.valueOf(parts[1])));
          } else if (parts[0].equals("MOVE")) {
            Player player = Player.valueOf(parts[1]);
            int row = Integer.parseInt(parts[2]);
            int col = Integer.parseInt(parts[3]);
            int flippedCount = Integer.parseInt(parts[4]);
            history.add(MoveRecord.move(moveNum, player, row, col, new ArrayList<>()));
          }
          moveNum++;
        }
      }

      return new LoadResult(boardSize, currentTurn, boardState, history);
    }
  }
}
