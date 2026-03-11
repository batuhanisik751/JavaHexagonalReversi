package cs3500.reversi;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs3500.reversi.controller.ReplayController;
import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for ReplayController — verifies stepping, boundaries, and flipped piece computation.
 */
public class ReplayControllerTest {

  private List<MoveRecord> moves;
  private int boardSize;

  @Before
  public void setUp() {
    // Play a few moves on a size-4 board to generate valid MoveRecords
    boardSize = 4;
    ReversiModel model = new ReversiModel(boardSize);
    moves = new ArrayList<>();

    // Find a valid move for BLACK and play it
    int[] firstMove = findValidMove(model, Player.BLACK);
    if (firstMove != null) {
      List<Coordinate> flipped1 = playAndGetFlipped(model, firstMove[0], firstMove[1],
              Player.BLACK);
      moves.add(MoveRecord.move(1, Player.BLACK, firstMove[0], firstMove[1], flipped1));

      // Find a valid move for WHITE and play it
      int[] secondMove = findValidMove(model, Player.WHITE);
      if (secondMove != null) {
        List<Coordinate> flipped2 = playAndGetFlipped(model, secondMove[0], secondMove[1],
                Player.WHITE);
        moves.add(MoveRecord.move(2, Player.WHITE, secondMove[0], secondMove[1], flipped2));
      }

      // Add a pass
      model.passTurn();
      moves.add(MoveRecord.pass(3, Player.BLACK));

      // One more move for WHITE if possible
      int[] thirdMove = findValidMove(model, Player.WHITE);
      if (thirdMove != null) {
        List<Coordinate> flipped3 = playAndGetFlipped(model, thirdMove[0], thirdMove[1],
                Player.WHITE);
        moves.add(MoveRecord.move(4, Player.WHITE, thirdMove[0], thirdMove[1], flipped3));
      }
    }
  }

  private int[] findValidMove(ReversiModel model, Player player) {
    for (int r = 0; r < model.getBoard().size(); r++) {
      for (int c = 0; c < model.getRow(r).size(); c++) {
        if (model.isValidMove(r, c, player)) {
          return new int[]{r, c};
        }
      }
    }
    return null;
  }

  private List<Coordinate> playAndGetFlipped(ReversiModel model, int row, int col, Player player) {
    int rows = model.getBoard().size();
    Player[][] before = new Player[rows][];
    for (int r = 0; r < rows; r++) {
      int cols = model.getRow(r).size();
      before[r] = new Player[cols];
      for (int c = 0; c < cols; c++) {
        before[r][c] = model.getSpaceContent(r, c);
      }
    }
    model.move(row, col, player);
    List<Coordinate> flipped = new ArrayList<>();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < model.getRow(r).size(); c++) {
        if (r == row && c == col) {
          continue;
        }
        Player after = model.getSpaceContent(r, c);
        if (after != null && after != before[r][c]) {
          flipped.add(new Coordinate(r, c));
        }
      }
    }
    return flipped;
  }

  // --- Initial state ---

  @Test
  public void testInitialStep() {
    ReplayController rc = new ReplayController(boardSize, moves);
    assertEquals(0, rc.getCurrentStep());
    assertNull(rc.getCurrentRecord());
    assertTrue(rc.getCurrentFlipped().isEmpty());
  }

  @Test
  public void testTotalSteps() {
    ReplayController rc = new ReplayController(boardSize, moves);
    assertEquals(moves.size(), rc.getTotalSteps());
  }

  @Test
  public void testInitialModelIsStartingPosition() {
    ReplayController rc = new ReplayController(boardSize, moves);
    IReadOnlyReversiModel model = rc.getModel();
    // Starting position has initial pieces
    assertEquals(boardSize, model.getBoardSize());
    assertEquals(Player.BLACK, model.getCurrentTurn());
  }

  // --- Step forward ---

  @Test
  public void testStepForward() {
    ReplayController rc = new ReplayController(boardSize, moves);
    assertTrue(rc.stepForward());
    assertEquals(1, rc.getCurrentStep());
    assertNotNull(rc.getCurrentRecord());
    assertEquals(moves.get(0).getRow(), rc.getCurrentRecord().getRow());
  }

  @Test
  public void testStepForwardUpdatesModel() {
    ReplayController rc = new ReplayController(boardSize, moves);
    IReadOnlyReversiModel before = rc.getModel();
    int scoreBefore = before.getScore(Player.BLACK) + before.getScore(Player.WHITE);
    rc.stepForward();
    // After a move, total pieces on board should increase by 1
    IReadOnlyReversiModel after = rc.getModel();
    int scoreAfter = after.getScore(Player.BLACK) + after.getScore(Player.WHITE);
    assertEquals(scoreBefore + 1, scoreAfter);
  }

  @Test
  public void testStepForwardAtEnd() {
    ReplayController rc = new ReplayController(boardSize, moves);
    // Step to the end
    for (int i = 0; i < moves.size(); i++) {
      assertTrue(rc.stepForward());
    }
    // One more should return false
    assertFalse(rc.stepForward());
    assertEquals(moves.size(), rc.getCurrentStep());
  }

  // --- Step back ---

  @Test
  public void testStepBackAtStart() {
    ReplayController rc = new ReplayController(boardSize, moves);
    assertFalse(rc.stepBack());
    assertEquals(0, rc.getCurrentStep());
  }

  @Test
  public void testStepBackAfterForward() {
    ReplayController rc = new ReplayController(boardSize, moves);
    rc.stepForward();
    rc.stepForward();
    assertTrue(rc.stepBack());
    assertEquals(1, rc.getCurrentStep());
  }

  @Test
  public void testStepBackRestoresModel() {
    ReplayController rc = new ReplayController(boardSize, moves);
    int initialBlack = rc.getModel().getScore(Player.BLACK);
    int initialWhite = rc.getModel().getScore(Player.WHITE);
    rc.stepForward();
    rc.stepBack();
    assertEquals(initialBlack, rc.getModel().getScore(Player.BLACK));
    assertEquals(initialWhite, rc.getModel().getScore(Player.WHITE));
  }

  // --- Go to start / end ---

  @Test
  public void testGoToEnd() {
    ReplayController rc = new ReplayController(boardSize, moves);
    rc.goToEnd();
    assertEquals(moves.size(), rc.getCurrentStep());
    assertFalse(rc.stepForward());
  }

  @Test
  public void testGoToStart() {
    ReplayController rc = new ReplayController(boardSize, moves);
    rc.goToEnd();
    rc.goToStart();
    assertEquals(0, rc.getCurrentStep());
    assertNull(rc.getCurrentRecord());
  }

  // --- Flipped pieces ---

  @Test
  public void testFlippedPiecesOnMove() {
    ReplayController rc = new ReplayController(boardSize, moves);
    rc.stepForward(); // First move (not a pass)
    if (!moves.get(0).isPass()) {
      assertFalse(rc.getCurrentFlipped().isEmpty());
    }
  }

  @Test
  public void testFlippedPiecesOnPass() {
    ReplayController rc = new ReplayController(boardSize, moves);
    // Find the pass move and step to it
    for (int i = 0; i < moves.size(); i++) {
      rc.stepForward();
      if (moves.get(i).isPass()) {
        assertTrue(rc.getCurrentFlipped().isEmpty());
        return;
      }
    }
  }

  // --- History up to current ---

  @Test
  public void testHistoryAtStart() {
    ReplayController rc = new ReplayController(boardSize, moves);
    assertTrue(rc.getHistoryUpToCurrent().isEmpty());
  }

  @Test
  public void testHistoryAfterSteps() {
    ReplayController rc = new ReplayController(boardSize, moves);
    rc.stepForward();
    rc.stepForward();
    assertEquals(2, rc.getHistoryUpToCurrent().size());
  }

  @Test
  public void testHistoryAtEnd() {
    ReplayController rc = new ReplayController(boardSize, moves);
    rc.goToEnd();
    assertEquals(moves.size(), rc.getHistoryUpToCurrent().size());
  }

  // --- Empty game ---

  @Test
  public void testEmptyMoveList() {
    ReplayController rc = new ReplayController(boardSize, Collections.emptyList());
    assertEquals(0, rc.getCurrentStep());
    assertEquals(0, rc.getTotalSteps());
    assertFalse(rc.stepForward());
    assertFalse(rc.stepBack());
  }

  // --- Round-trip ---

  @Test
  public void testFullForwardThenFullBackward() {
    ReplayController rc = new ReplayController(boardSize, moves);
    int initialBlack = rc.getModel().getScore(Player.BLACK);
    int initialWhite = rc.getModel().getScore(Player.WHITE);

    // Go all the way forward
    while (rc.stepForward()) { }

    // Go all the way back
    while (rc.stepBack()) { }

    assertEquals(0, rc.getCurrentStep());
    assertEquals(initialBlack, rc.getModel().getScore(Player.BLACK));
    assertEquals(initialWhite, rc.getModel().getScore(Player.WHITE));
  }
}
