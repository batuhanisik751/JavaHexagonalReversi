package cs3500.reversi.view;

import org.junit.Before;
import org.junit.Test;

import javafx.scene.input.KeyCode;

import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.ReversiModel;

import static org.junit.Assert.assertEquals;

/**
 * Tests for keyboard navigation logic in FxReversiCanvas.
 * Tests the static computeNextPosition method directly without JavaFX initialization.
 */
public class HexKeyNavTest {

  private IReadOnlyReversiModel model;

  @Before
  public void setUp() {
    // Board size 4: rows have lengths [4, 5, 6, 7, 6, 5, 4], middle row index = 3
    model = new ReversiModel(4);
  }

  // --- Initial cursor placement (from no selection) ---

  @Test
  public void testInitialPositionGoesToCenter() {
    Coordinate result = FxReversiCanvas.computeNextPosition(model, -1, -1, KeyCode.UP);
    // Middle row = boardSize - 1 = 3, middle col = 7 / 2 = 3
    assertEquals(3, result.getRow());
    assertEquals(3, result.getCol());
  }

  @Test
  public void testInitialPositionAnyArrowKey() {
    for (KeyCode dir : new KeyCode[]{KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT}) {
      Coordinate result = FxReversiCanvas.computeNextPosition(model, -1, -1, dir);
      assertEquals(3, result.getRow());
      assertEquals(3, result.getCol());
    }
  }

  // --- LEFT / RIGHT navigation ---

  @Test
  public void testMoveLeft() {
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 3, 3, KeyCode.LEFT);
    assertEquals(3, result.getRow());
    assertEquals(2, result.getCol());
  }

  @Test
  public void testMoveRight() {
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 3, 3, KeyCode.RIGHT);
    assertEquals(3, result.getRow());
    assertEquals(4, result.getCol());
  }

  @Test
  public void testMoveLeftAtBoundary() {
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 3, 0, KeyCode.LEFT);
    assertEquals(3, result.getRow());
    assertEquals(0, result.getCol());
  }

  @Test
  public void testMoveRightAtBoundary() {
    // Row 3 (middle) has 7 hexes, max col = 6
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 3, 6, KeyCode.RIGHT);
    assertEquals(3, result.getRow());
    assertEquals(6, result.getCol());
  }

  // --- UP / DOWN navigation ---

  @Test
  public void testMoveUpFromCenter() {
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 3, 3, KeyCode.UP);
    assertEquals(2, result.getRow());
    // Row 2 has 6 hexes; col 3 in row 3 maps to col 3 or 2 in row 2
    assertEquals(true, result.getCol() >= 0 && result.getCol() < 6);
  }

  @Test
  public void testMoveDownFromCenter() {
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 3, 3, KeyCode.DOWN);
    assertEquals(4, result.getRow());
    // Row 4 has 6 hexes
    assertEquals(true, result.getCol() >= 0 && result.getCol() < 6);
  }

  @Test
  public void testMoveUpAtTopBoundary() {
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 0, 0, KeyCode.UP);
    assertEquals(0, result.getRow());
    assertEquals(0, result.getCol());
  }

  @Test
  public void testMoveDownAtBottomBoundary() {
    // Row 6 is the last row (board has 7 rows for size 4)
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 6, 0, KeyCode.DOWN);
    assertEquals(6, result.getRow());
    assertEquals(0, result.getCol());
  }

  // --- Column alignment across rows of different lengths ---

  @Test
  public void testUpFromLongerToShorterRow() {
    // From row 3 (7 hexes) col 0, go up to row 2 (6 hexes)
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 3, 0, KeyCode.UP);
    assertEquals(2, result.getRow());
    assertEquals(0, result.getCol());
  }

  @Test
  public void testUpFromLongerToShorterRowLastCol() {
    // From row 3 (7 hexes) col 6, go up to row 2 (6 hexes)
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 3, 6, KeyCode.UP);
    assertEquals(2, result.getRow());
    assertEquals(5, result.getCol()); // clamped to last col of row 2
  }

  @Test
  public void testDownFromShorterToLongerRow() {
    // From row 2 (6 hexes) col 0, go down to row 3 (7 hexes)
    // Col shifts due to hex offset alignment
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 2, 0, KeyCode.DOWN);
    assertEquals(3, result.getRow());
    assertEquals(true, result.getCol() >= 0 && result.getCol() <= 1);
  }

  @Test
  public void testUpFromTopRowShortest() {
    // From row 1 (5 hexes) col 2, go up to row 0 (4 hexes)
    Coordinate result = FxReversiCanvas.computeNextPosition(model, 1, 2, KeyCode.UP);
    assertEquals(0, result.getRow());
    assertEquals(true, result.getCol() >= 0 && result.getCol() <= 3);
  }

  // --- Round-trip consistency ---

  @Test
  public void testUpThenDownReturnsToSameRow() {
    Coordinate up = FxReversiCanvas.computeNextPosition(model, 3, 3, KeyCode.UP);
    Coordinate back = FxReversiCanvas.computeNextPosition(model, up.getRow(), up.getCol(),
            KeyCode.DOWN);
    assertEquals(3, back.getRow());
  }

  // --- Different board size ---

  @Test
  public void testNavigationOnSmallBoard() {
    IReadOnlyReversiModel small = new ReversiModel(3);
    // Board size 3: rows have lengths [3, 4, 5, 4, 3], middle row = 2
    Coordinate result = FxReversiCanvas.computeNextPosition(small, -1, -1, KeyCode.RIGHT);
    assertEquals(2, result.getRow());
    assertEquals(2, result.getCol());
  }

  @Test
  public void testLeftRightTraversalStaysInRow() {
    // Walk right across the middle row
    int row = 3;
    int col = 0;
    for (int i = 0; i < 6; i++) {
      Coordinate next = FxReversiCanvas.computeNextPosition(model, row, col, KeyCode.RIGHT);
      assertEquals(row, next.getRow());
      assertEquals(col + 1, next.getCol());
      col = next.getCol();
    }
    // Now at col 6, moving right should stay
    Coordinate last = FxReversiCanvas.computeNextPosition(model, row, col, KeyCode.RIGHT);
    assertEquals(6, last.getCol());
  }
}
