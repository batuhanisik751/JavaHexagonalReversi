package cs3500.reversi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.model.SquareBoardShape;
import cs3500.reversi.view.TextView;

/**
 * Tests for full games on a square (Othello) board.
 */
public class SquareGameTest {
  private IReversiModel model;

  @Before
  public void setUp() {
    model = new ReversiModel(4, new SquareBoardShape());
  }

  @Test
  public void testSquareModelInitialization() {
    Assert.assertEquals(4, model.getBoardSize());
    Assert.assertEquals(4, model.getBoard().size());
    for (int r = 0; r < 4; r++) {
      Assert.assertEquals(4, model.getRow(r).size());
    }
    Assert.assertEquals(Player.BLACK, model.getCurrentTurn());
  }

  @Test
  public void testSquareInitialPieceCount() {
    Assert.assertEquals(2, model.getScore(Player.BLACK));
    Assert.assertEquals(2, model.getScore(Player.WHITE));
  }

  @Test
  public void testSquareInitialPiecePlacement() {
    // On a 4x4 board, center is at (1,1),(1,2),(2,1),(2,2)
    Assert.assertEquals(Player.WHITE, model.getSpaceContent(1, 1));
    Assert.assertEquals(Player.BLACK, model.getSpaceContent(1, 2));
    Assert.assertEquals(Player.BLACK, model.getSpaceContent(2, 1));
    Assert.assertEquals(Player.WHITE, model.getSpaceContent(2, 2));
  }

  @Test
  public void testSquareValidMoveExists() {
    // Black should have valid moves on initial board
    Assert.assertFalse(model.noValidMoves(Player.BLACK));
  }

  @Test
  public void testSquareValidMoveLocations() {
    // On 4x4 with standard Othello setup, Black can play at positions
    // that sandwich White pieces
    Assert.assertTrue(model.isValidMove(0, 1, Player.BLACK));
    Assert.assertTrue(model.isValidMove(1, 0, Player.BLACK));
    Assert.assertTrue(model.isValidMove(2, 3, Player.BLACK));
    Assert.assertTrue(model.isValidMove(3, 2, Player.BLACK));
  }

  @Test
  public void testSquareInvalidMoveOnOccupied() {
    Assert.assertFalse(model.isValidMove(1, 1, Player.BLACK));
  }

  @Test
  public void testSquareInvalidMoveNoFlip() {
    Assert.assertFalse(model.isValidMove(0, 0, Player.BLACK));
  }

  @Test
  public void testSquareMoveFlipsPieces() {
    // Black plays at (0,1) — should flip W at (1,1)
    model.move(0, 1, Player.BLACK);
    Assert.assertEquals(Player.BLACK, model.getSpaceContent(0, 1));
    Assert.assertEquals(Player.BLACK, model.getSpaceContent(1, 1));
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
  }

  @Test
  public void testSquareScoreAfterMove() {
    model.move(0, 1, Player.BLACK);
    // Black had 2, placed 1, flipped 1 = 4
    Assert.assertEquals(4, model.getScore(Player.BLACK));
    // White had 2, lost 1 = 1
    Assert.assertEquals(1, model.getScore(Player.WHITE));
  }

  @Test
  public void testSquarePassTurn() {
    model.passTurn();
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
  }

  @Test
  public void testSquareGameNotOverInitially() {
    Assert.assertFalse(model.gameOver());
  }

  @Test
  public void testSquare8x8Model() {
    IReversiModel big = new ReversiModel(8, new SquareBoardShape());
    Assert.assertEquals(8, big.getBoard().size());
    Assert.assertEquals(2, big.getScore(Player.BLACK));
    Assert.assertEquals(2, big.getScore(Player.WHITE));
    Assert.assertFalse(big.noValidMoves(Player.BLACK));
  }

  @Test
  public void testSquareCopyModel() {
    IReversiModel copy = model.copyModel();
    Assert.assertEquals(model.getBoardSize(), copy.getBoardSize());
    Assert.assertEquals("square", copy.getBoardShape().getShapeName());
    Assert.assertEquals(model.getCurrentTurn(), copy.getCurrentTurn());
    // Modify original, copy should be independent
    model.move(0, 1, Player.BLACK);
    Assert.assertNotEquals(model.getCurrentTurn(), copy.getCurrentTurn());
  }

  @Test
  public void testSquareTextView() {
    TextView tv = new TextView(model);
    String text = tv.toString();
    Assert.assertNotNull(text);
    Assert.assertTrue(text.contains("X"));
    Assert.assertTrue(text.contains("O"));
    Assert.assertTrue(text.contains("_"));
  }

  @Test
  public void testSquareMultipleMoves() {
    // Play a sequence of moves
    model.move(0, 1, Player.BLACK);
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
    model.move(0, 0, Player.WHITE);
    Assert.assertEquals(Player.BLACK, model.getCurrentTurn());
    // Game should still be going
    Assert.assertFalse(model.gameOver());
  }

  @Test(expected = IllegalStateException.class)
  public void testSquareOutOfTurnMove() {
    model.move(0, 1, Player.WHITE); // Should fail, it's BLACK's turn
  }
}
