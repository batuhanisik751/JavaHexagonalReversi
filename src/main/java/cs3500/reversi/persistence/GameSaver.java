package cs3500.reversi.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import cs3500.reversi.history.GameHistory;
import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.Player;

/**
 * Serializes a Reversi game state and history to a plain-text .reversi file.
 */
public final class GameSaver {

  /**
   * Saves the current game state and history to the given file.
   * @param model the game model to save.
   * @param history the game history to save.
   * @param file the file to write to.
   * @throws IOException if writing fails.
   */
  public static void save(IReadOnlyReversiModel model, GameHistory history, File file)
          throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      writer.write("BOARD_SIZE:" + model.getBoardSize());
      writer.newLine();
      writer.write("BOARD_SHAPE:" + model.getBoardShape().getShapeName());
      writer.newLine();
      writer.write("CURRENT_TURN:" + model.getCurrentTurn().name());
      writer.newLine();
      writer.write("BOARD:");
      writer.newLine();
      for (int r = 0; r < model.getBoard().size(); r++) {
        StringBuilder row = new StringBuilder();
        for (int c = 0; c < model.getRow(r).size(); c++) {
          if (c > 0) {
            row.append(",");
          }
          Player p = model.getSpaceContent(r, c);
          if (p == null) {
            row.append("_");
          } else if (p == Player.BLACK) {
            row.append("B");
          } else {
            row.append("W");
          }
        }
        writer.write(row.toString());
        writer.newLine();
      }
      writer.write("HISTORY:");
      writer.newLine();
      List<MoveRecord> records = history.getRecords();
      for (MoveRecord record : records) {
        if (record.isPass()) {
          writer.write("PASS," + record.getPlayer().name());
        } else {
          writer.write("MOVE," + record.getPlayer().name() + ","
                  + record.getRow() + "," + record.getCol() + ","
                  + record.getFlipped().size());
        }
        writer.newLine();
      }
    }
  }
}
