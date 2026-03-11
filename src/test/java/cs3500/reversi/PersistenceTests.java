package cs3500.reversi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import cs3500.reversi.history.GameHistory;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.persistence.GameLoader;
import cs3500.reversi.persistence.GameSaver;
import cs3500.reversi.persistence.LoadResult;

/**
 * Tests for GameSaver and GameLoader — round-trips, corrupt files, and edge cases.
 */
public class PersistenceTests {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private IReversiModel model;
  private GameHistory history;

  @Before
  public void setUp() {
    model = new ReversiModel(3);
    history = new GameHistory();
  }

  // ---- Round-trip Tests ----

  @Test
  public void testSaveAndLoadInitialState() throws IOException {
    File file = tempFolder.newFile("initial.reversi");
    GameSaver.save(model, history, file);
    LoadResult result = GameLoader.load(file);
    Assert.assertEquals(3, result.getBoardSize());
    Assert.assertEquals(Player.BLACK, result.getCurrentTurn());
    Assert.assertTrue(result.getHistory().isEmpty());
    // Verify board dimensions
    int totalRows = (3 * 2) - 1;
    Assert.assertEquals(totalRows, result.getBoardState().length);
  }

  @Test
  public void testSaveAndLoadAfterMoves() throws IOException {
    // Make some moves
    model.move(1, 3, model.getCurrentTurn()); // BLACK
    history.recordMove(Player.BLACK, 1, 3, Arrays.asList(new Coordinate(1, 2)));
    model.move(3, 0, model.getCurrentTurn()); // WHITE
    history.recordMove(Player.WHITE, 3, 0, Arrays.asList(new Coordinate(2, 1)));

    File file = tempFolder.newFile("after_moves.reversi");
    GameSaver.save(model, history, file);
    LoadResult result = GameLoader.load(file);

    Assert.assertEquals(3, result.getBoardSize());
    Assert.assertEquals(model.getCurrentTurn(), result.getCurrentTurn());
    Assert.assertEquals(2, result.getHistory().size());

    // Verify board state matches
    for (int r = 0; r < model.getBoard().size(); r++) {
      for (int c = 0; c < model.getRow(r).size(); c++) {
        Assert.assertEquals("Mismatch at (" + r + "," + c + ")",
                model.getSpaceContent(r, c), result.getBoardState()[r][c]);
      }
    }
  }

  @Test
  public void testSaveAndLoadWithPasses() throws IOException {
    model.passTurn();
    history.recordPass(Player.BLACK);

    File file = tempFolder.newFile("with_pass.reversi");
    GameSaver.save(model, history, file);
    LoadResult result = GameLoader.load(file);

    Assert.assertEquals(1, result.getHistory().size());
    Assert.assertTrue(result.getHistory().get(0).isPass());
    Assert.assertEquals(Player.BLACK, result.getHistory().get(0).getPlayer());
  }

  @Test
  public void testSaveAndLoadPreservesTurn() throws IOException {
    model.move(1, 3, model.getCurrentTurn()); // BLACK moves, turn becomes WHITE
    history.recordMove(Player.BLACK, 1, 3, new ArrayList<>());

    File file = tempFolder.newFile("turn.reversi");
    GameSaver.save(model, history, file);
    LoadResult result = GameLoader.load(file);

    Assert.assertEquals(Player.WHITE, result.getCurrentTurn());
  }

  @Test
  public void testRoundTripLargerBoard() throws IOException {
    IReversiModel bigModel = new ReversiModel(5);
    GameHistory bigHistory = new GameHistory();
    bigModel.move(3, 5, bigModel.getCurrentTurn());
    bigHistory.recordMove(Player.BLACK, 3, 5, new ArrayList<>());

    File file = tempFolder.newFile("big.reversi");
    GameSaver.save(bigModel, bigHistory, file);
    LoadResult result = GameLoader.load(file);

    Assert.assertEquals(5, result.getBoardSize());
    int totalRows = (5 * 2) - 1;
    Assert.assertEquals(totalRows, result.getBoardState().length);
    Assert.assertEquals(1, result.getHistory().size());
  }

  @Test
  public void testRoundTripBoardContentIntegrity() throws IOException {
    // Set up a known board state via model.loadState
    model.move(1, 3, model.getCurrentTurn());
    history.recordMove(Player.BLACK, 1, 3, new ArrayList<>());

    File file = tempFolder.newFile("integrity.reversi");
    GameSaver.save(model, history, file);
    LoadResult result = GameLoader.load(file);

    // Load result into a fresh model and verify equality
    IReversiModel loaded = new ReversiModel(3);
    loaded.loadState(result.getCurrentTurn(), result.getBoardState());

    for (int r = 0; r < model.getBoard().size(); r++) {
      for (int c = 0; c < model.getRow(r).size(); c++) {
        Assert.assertEquals("Board mismatch at (" + r + "," + c + ")",
                model.getSpaceContent(r, c), loaded.getSpaceContent(r, c));
      }
    }
    Assert.assertEquals(model.getCurrentTurn(), loaded.getCurrentTurn());
  }

  @Test
  public void testSaveAndLoadMixedHistory() throws IOException {
    model.move(1, 3, model.getCurrentTurn()); // BLACK
    history.recordMove(Player.BLACK, 1, 3, Arrays.asList(new Coordinate(1, 2)));
    model.move(3, 0, model.getCurrentTurn()); // WHITE
    history.recordMove(Player.WHITE, 3, 0, new ArrayList<>());
    model.passTurn(); // BLACK passes
    history.recordPass(Player.BLACK);

    File file = tempFolder.newFile("mixed.reversi");
    GameSaver.save(model, history, file);
    LoadResult result = GameLoader.load(file);

    Assert.assertEquals(3, result.getHistory().size());
    Assert.assertFalse(result.getHistory().get(0).isPass());
    Assert.assertFalse(result.getHistory().get(1).isPass());
    Assert.assertTrue(result.getHistory().get(2).isPass());
  }

  // ---- Corrupt File Tests ----

  @Test
  public void testLoadMissingBoardSize() throws IOException {
    File file = tempFolder.newFile("corrupt1.reversi");
    writeFile(file, "CURRENT_TURN:BLACK\nBOARD:\n_,_,_\n");
    try {
      GameLoader.load(file);
      Assert.fail("Expected IOException for missing BOARD_SIZE");
    } catch (IOException e) {
      Assert.assertTrue(e.getMessage().contains("BOARD_SIZE"));
    }
  }

  @Test
  public void testLoadMissingCurrentTurn() throws IOException {
    File file = tempFolder.newFile("corrupt2.reversi");
    writeFile(file, "BOARD_SIZE:3\nBOARD:\n_,_,_\n");
    try {
      GameLoader.load(file);
      Assert.fail("Expected IOException for missing CURRENT_TURN");
    } catch (IOException e) {
      Assert.assertTrue(e.getMessage().contains("CURRENT_TURN"));
    }
  }

  @Test
  public void testLoadMissingBoardHeader() throws IOException {
    File file = tempFolder.newFile("corrupt3.reversi");
    writeFile(file, "BOARD_SIZE:3\nCURRENT_TURN:BLACK\n_,_,_\n");
    try {
      GameLoader.load(file);
      Assert.fail("Expected IOException for missing BOARD header");
    } catch (IOException e) {
      Assert.assertTrue(e.getMessage().contains("BOARD"));
    }
  }

  @Test
  public void testLoadInvalidCellValue() throws IOException {
    File file = tempFolder.newFile("corrupt4.reversi");
    writeFile(file, "BOARD_SIZE:3\nCURRENT_TURN:BLACK\nBOARD:\nX,_,_\n_,_,_,_\n_,_,_,_,_\n_,_,_,_\n_,_,_\n");
    try {
      GameLoader.load(file);
      Assert.fail("Expected IOException for invalid cell value");
    } catch (IOException e) {
      Assert.assertTrue(e.getMessage().contains("Invalid cell"));
    }
  }

  @Test
  public void testLoadTruncatedBoardRows() throws IOException {
    File file = tempFolder.newFile("corrupt5.reversi");
    // Board size 3 needs 5 rows, but only provide 2
    writeFile(file, "BOARD_SIZE:3\nCURRENT_TURN:BLACK\nBOARD:\n_,_,_\n_,_,_,_\n");
    try {
      GameLoader.load(file);
      Assert.fail("Expected IOException for missing board rows");
    } catch (IOException e) {
      Assert.assertTrue(e.getMessage().contains("missing board row"));
    }
  }

  @Test
  public void testLoadEmptyFile() throws IOException {
    File file = tempFolder.newFile("empty.reversi");
    writeFile(file, "");
    try {
      GameLoader.load(file);
      Assert.fail("Expected IOException for empty file");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void testLoadInvalidTurnValue() throws IOException {
    File file = tempFolder.newFile("bad_turn.reversi");
    writeFile(file, "BOARD_SIZE:3\nCURRENT_TURN:BLUE\nBOARD:\n_,_,_\n");
    try {
      GameLoader.load(file);
      Assert.fail("Expected exception for invalid turn value");
    } catch (Exception e) {
      // IllegalArgumentException from Player.valueOf or IOException
    }
  }

  // ---- History-less Save/Load ----

  @Test
  public void testLoadFileWithoutHistorySection() throws IOException {
    File file = tempFolder.newFile("no_history.reversi");
    StringBuilder sb = new StringBuilder();
    sb.append("BOARD_SIZE:3\n");
    sb.append("CURRENT_TURN:BLACK\n");
    sb.append("BOARD:\n");
    sb.append("_,_,_\n");
    sb.append("_,B,W,_\n");
    sb.append("_,W,B,W,B\n");
    sb.append("_,B,W,_\n");
    sb.append("_,_,_\n");
    writeFile(file, sb.toString());

    LoadResult result = GameLoader.load(file);
    Assert.assertEquals(3, result.getBoardSize());
    Assert.assertTrue(result.getHistory().isEmpty());
  }

  @Test
  public void testSaveAndLoadEmptyHistory() throws IOException {
    File file = tempFolder.newFile("empty_history.reversi");
    GameSaver.save(model, history, file);
    LoadResult result = GameLoader.load(file);
    Assert.assertTrue(result.getHistory().isEmpty());
  }

  // ---- Helper ----

  private void writeFile(File file, String content) throws IOException {
    try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
      w.write(content);
    }
  }
}
