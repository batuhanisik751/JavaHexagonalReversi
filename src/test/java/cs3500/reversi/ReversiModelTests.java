package cs3500.reversi;

import org.junit.Assert;
import org.junit.Test;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.view.ITextView;
import cs3500.reversi.view.TextView;



/**
 * The test class ReversiModelTests.
 */
public class ReversiModelTests {
  IReversiModel model3 = new ReversiModel(3);
  ITextView view3 = new TextView(model3);
  IReversiModel model4 = new ReversiModel(4);
  ITextView view4 = new TextView(model4);

  // Test starting game with invalid board size
  @Test
  public void testInvalidBoardSize() {
    Assert.assertThrows(IllegalArgumentException.class, () -> new ReversiModel(2));
    Assert.assertThrows(IllegalArgumentException.class, () -> new ReversiModel(-1));
    Assert.assertThrows(IllegalArgumentException.class, () -> new ReversiModel(0));
  }

  // Tests the board size matches the given size.
  @Test
  public void testCorrectBoardSize() {
    Assert.assertEquals(3, model3.getBoardSize());
  }

  // Test that the list of Spaces within the board are the right size with a board size of 3
  @Test
  public void testBoardListSizesBoardSize3() {
    Assert.assertEquals(5, model3.getBoard().size());
    Assert.assertEquals(3, model3.getBoard().get(0).size());
    Assert.assertEquals(4, model3.getBoard().get(1).size());
    Assert.assertEquals(5, model3.getBoard().get(2).size());
    Assert.assertEquals(4, model3.getBoard().get(3).size());
    Assert.assertEquals(3, model3.getBoard().get(4).size());
  }

  // Test that the list of Spaces within the board are the right size with a board size of 6
  @Test
  public void testBoardListSizesBoardSize6() {
    IReversiModel model = new ReversiModel(6);
    Assert.assertEquals(11, model.getBoard().size());
    Assert.assertEquals(6, model.getBoard().get(0).size());
    Assert.assertEquals(7, model.getBoard().get(1).size());
    Assert.assertEquals(8, model.getBoard().get(2).size());
    Assert.assertEquals(9, model.getBoard().get(3).size());
    Assert.assertEquals(10, model.getBoard().get(4).size());
    Assert.assertEquals(11, model.getBoard().get(5).size());
    Assert.assertEquals(10, model.getBoard().get(6).size());
    Assert.assertEquals(9, model.getBoard().get(7).size());
    Assert.assertEquals(8, model.getBoard().get(8).size());
    Assert.assertEquals(7, model.getBoard().get(9).size());
    Assert.assertEquals(6, model.getBoard().get(10).size());
  }

  // Tests the initial board as a String
  @Test
  public void testInitialBoardString() {
    Assert.assertEquals(
            "   _ _ _    \n" + "  _ X O _   \n"
                    + " _ O _ X _  \n" + "  _ X O _   \n" + "   _ _ _    \n", view3.toString());

  }

  // Test getRow
  @Test
  public void testGetRow() {
    Assert.assertEquals(3, model3.getRow(0).size());
    Assert.assertEquals(4, model3.getRow(1).size());
    Assert.assertEquals(5, model3.getRow(2).size());
    Assert.assertEquals(4, model3.getRow(3).size());
    Assert.assertEquals(3, model3.getRow(4).size());
  }

  // Test getSpaceContents
  @Test
  public void testGetSpaceContents() {
    Assert.assertThrows(IllegalArgumentException.class, () ->
            model3.getSpaceContent(0, 4));
    Assert.assertThrows(IllegalArgumentException.class, () ->
            model3.getSpaceContent(6, 2));
    Assert.assertNull(model3.getSpaceContent(0, 0));
    model3.getSpace(0, 0).setFilled(Player.BLACK);
    Assert.assertEquals(Player.BLACK, model3.getSpaceContent(0, 0));
  }

  // Test invalid arguments to getSpace
  @Test
  public void testGetSpaceContentsInvalidArg() {
    Assert.assertThrows(IllegalArgumentException.class, () ->
            model3.getSpace(0, 3));
    Assert.assertThrows(IllegalArgumentException.class, () ->
            model3.getSpace(5, 2));
  }

  // Test getSpace works properly when filling a model.Space
  @Test
  public void testGetSpace() {
    Assert.assertTrue(model3.getSpace(0, 0).isEmpty());
    model3.getSpace(0, 0).setFilled(Player.BLACK);
    Assert.assertFalse(model3.getSpace(0, 0).isEmpty());
  }

  // Test fillSpace with an invalid/already filled space
  @Test
  public void testInvalidFillSpace() {
    ReversiModel localModel = new ReversiModel(3);
    Assert.assertThrows(IllegalArgumentException.class, () ->
            localModel.fillSpace(0, 3));
    Assert.assertThrows(IllegalArgumentException.class, () ->
            localModel.fillSpace(5, 2));
    localModel.fillSpace(3, 3);
    Assert.assertThrows(IllegalStateException.class, () ->
            localModel.fillSpace(3, 3));
  }

  // Test fillSpace works properly
  @Test
  public void testFillSpace() {
    ReversiModel localModel = new ReversiModel(3);
    Assert.assertEquals(Player.BLACK, localModel.getCurrentTurn());
    Assert.assertTrue(localModel.getSpace(3, 3).isEmpty());
    localModel.fillSpace(3, 3);
    Assert.assertFalse(localModel.getSpace(3, 3).isEmpty());
    Assert.assertEquals(Player.WHITE, localModel.getCurrentTurn());
  }

  // Test that passTurn works properly
  @Test
  public void testPassTurn() {
    Assert.assertEquals(Player.BLACK, model3.getCurrentTurn());
    model3.passTurn();
    Assert.assertEquals(Player.WHITE, model3.getCurrentTurn());
    model3.passTurn();
    Assert.assertEquals(Player.BLACK, model3.getCurrentTurn());
  }

  // Check for valid and invalid moves using validMove
  @Test
  public void testValidMove() {
    Assert.assertEquals(Player.BLACK, model4.getCurrentTurn());
    // valid (horizontal line)
    Assert.assertTrue(model4.isValidMove(2, 4, model4.getCurrentTurn()));
    // valid (diagonal line)
    Assert.assertTrue(model4.isValidMove(1, 2, model4.getCurrentTurn()));
    // invalid (does not make line)
    Assert.assertFalse(model4.isValidMove(3, 1, model4.getCurrentTurn()));
    // invalid (next to same model.Player)
    Assert.assertFalse(model4.isValidMove(3, 5, model4.getCurrentTurn()));
    // invalid (move to a non-empty)
    Assert.assertFalse(model4.isValidMove(2, 2, model4.getCurrentTurn()));
    model4.passTurn();
    Assert.assertEquals(Player.WHITE, model4.getCurrentTurn());
    // invalid (not next to filled spaces)
    Assert.assertFalse(model4.isValidMove(0, 0, model4.getCurrentTurn()));
    // invalid (does not make a line with opposite model.Player)
    Assert.assertFalse(model4.isValidMove(3, 1, model4.getCurrentTurn()));
    Assert.assertTrue(model4.isValidMove(4, 1, model4.getCurrentTurn()));
    // valid (horizontal line)
    Assert.assertTrue(model4.isValidMove(2, 1, model4.getCurrentTurn()));
  }

  // Test Player cannot move outside of turn
  @Test
  public void testMoveOutsideOfTurn() {
    Assert.assertTrue(model4.move(2, 4, model4.getCurrentTurn()));
    Assert.assertThrows(IllegalStateException.class, () ->
            model4.move(3, 1, Player.BLACK));
    Assert.assertTrue(model4.move(4, 1, model4.getCurrentTurn()));
    Assert.assertThrows(IllegalStateException.class, () ->
            model4.move(0, 2, Player.WHITE));
  }

  // Test that move works properly, with flipping other Player pieces
  @Test
  public void testMove() {
    Assert.assertTrue(model4.move(2, 4, model4.getCurrentTurn()));
    Assert.assertEquals("    _ _ _ _     \n" +
            "   _ _ _ _ _    \n" +
            "  _ _ X X X _   \n" +
            " _ _ O _ X _ _  \n" +
            "  _ _ X O _ _   \n" +
            "   _ _ _ _ _    \n" +
            "    _ _ _ _     \n", view4.toString());
    Assert.assertTrue(model4.move(4, 1, model4.getCurrentTurn()));
    Assert.assertEquals("    _ _ _ _     \n" +
            "   _ _ _ _ _    \n" +
            "  _ _ X X X _   \n" +
            " _ _ O _ X _ _  \n" +
            "  _ O O O _ _   \n" +
            "   _ _ _ _ _    \n" +
            "    _ _ _ _     \n", view4.toString());
  }

  // Test gameOver with no more moves
  @Test
  public void testGameOver() {
    model3.move(1, 3, model3.getCurrentTurn());
    model3.move(3, 0, model3.getCurrentTurn());
    model3.passTurn();
    model3.move(0, 1, model3.getCurrentTurn());
    model3.move(1, 0, model3.getCurrentTurn());
    model3.passTurn();
    model3.move(4, 1, model3.getCurrentTurn());
    model3.move(3, 3, model3.getCurrentTurn());
    Assert.assertEquals(Player.BLACK, model3.getCurrentTurn());
    Assert.assertTrue(model3.gameOver());
  }

  // Test getScore works properly
  @Test
  public void testGetScore() {
    Assert.assertEquals(model4.getScore(model4.getCurrentTurn()), 3);
    model4.passTurn();
    Assert.assertEquals(model4.getScore(model4.getCurrentTurn()), 3);
    model4.passTurn();
    model4.move(2, 4, model4.getCurrentTurn());
    Assert.assertEquals(model4.getScore(Player.BLACK), 5);
    Assert.assertEquals(model4.getScore(model4.getCurrentTurn()), 2);
  }


  // Test that the model does not change when the copy changes.
  @Test
  public void testModelDoesNotChangeWhenCopyChanges() {
    IReversiModel copyModel = model4.copyModel();
    ITextView viewCopy = new TextView(copyModel);
    copyModel.move(2, 4, copyModel.getCurrentTurn());
    copyModel.move(4, 1, copyModel.getCurrentTurn());
    Assert.assertNotEquals(viewCopy.toString(), view4.toString());
  }


  @Test
  public void testGetOpponentScore() {
    Assert.assertEquals(3, model4.getOpponentScore(model4.getCurrentTurn()));
    model4.move(2, 4, model4.getCurrentTurn());
    Assert.assertEquals(2, model4.getScore(model4.getCurrentTurn()));
  }
}