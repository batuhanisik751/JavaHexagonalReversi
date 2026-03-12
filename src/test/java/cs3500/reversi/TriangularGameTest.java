package cs3500.reversi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.model.TriangularBoardShape;
import cs3500.reversi.view.TextView;

/**
 * Tests for full games on a triangular board.
 */
public class TriangularGameTest {
  private IReversiModel model;

  @Before
  public void setUp() {
    model = new ReversiModel(5, new TriangularBoardShape());
  }

  @Test
  public void testTriangularModelInitialization() {
    Assert.assertEquals(5, model.getBoardSize());
    Assert.assertEquals(5, model.getBoard().size());
    Assert.assertEquals(1, model.getRow(0).size());
    Assert.assertEquals(3, model.getRow(1).size());
    Assert.assertEquals(5, model.getRow(2).size());
    Assert.assertEquals(7, model.getRow(3).size());
    Assert.assertEquals(9, model.getRow(4).size());
  }

  @Test
  public void testTriangularStartingTurn() {
    Assert.assertEquals(Player.BLACK, model.getCurrentTurn());
  }

  @Test
  public void testTriangularInitialPieces() {
    // Should have some initial pieces placed
    int totalPieces = model.getScore(Player.BLACK) + model.getScore(Player.WHITE);
    Assert.assertTrue(totalPieces >= 4);
    Assert.assertEquals(model.getScore(Player.BLACK), model.getScore(Player.WHITE));
  }

  @Test
  public void testTriangularShapeName() {
    Assert.assertEquals("triangular", model.getBoardShape().getShapeName());
  }

  @Test
  public void testTriangularGameNotOverInitially() {
    Assert.assertFalse(model.gameOver());
  }

  @Test
  public void testTriangularPassTurn() {
    model.passTurn();
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
    model.passTurn();
    Assert.assertEquals(Player.BLACK, model.getCurrentTurn());
  }

  @Test
  public void testTriangularCopyModel() {
    IReversiModel copy = model.copyModel();
    Assert.assertEquals("triangular", copy.getBoardShape().getShapeName());
    Assert.assertEquals(model.getBoardSize(), copy.getBoardSize());
    Assert.assertEquals(model.getBoard().size(), copy.getBoard().size());
  }

  @Test
  public void testTriangularTextView() {
    TextView tv = new TextView(model);
    String text = tv.toString();
    Assert.assertNotNull(text);
    // Should have proper triangular indentation
    String[] lines = text.split("\n");
    Assert.assertEquals(5, lines.length);
  }

  @Test
  public void testTriangularSize4() {
    IReversiModel small = new ReversiModel(4, new TriangularBoardShape());
    Assert.assertEquals(4, small.getBoard().size());
    Assert.assertEquals(1, small.getRow(0).size());
    Assert.assertEquals(3, small.getRow(1).size());
    Assert.assertEquals(5, small.getRow(2).size());
    Assert.assertEquals(7, small.getRow(3).size());
  }

  @Test
  public void testTriangularBoundsRespected() {
    // Should not be able to make moves out of bounds
    Assert.assertFalse(model.isValidMove(-1, 0, Player.BLACK));
    Assert.assertFalse(model.isValidMove(0, 1, Player.BLACK)); // Row 0 only has 1 cell
    Assert.assertFalse(model.isValidMove(5, 0, Player.BLACK));
  }

  @Test
  public void testTriangularDirectionsRespected() {
    // Triangular cells only have 3 directions
    // Verify that moves only flip along valid directions
    Assert.assertEquals(3, model.getBoardShape().getDirections(2, 0).size());
    Assert.assertEquals(3, model.getBoardShape().getDirections(2, 1).size());
  }
}
