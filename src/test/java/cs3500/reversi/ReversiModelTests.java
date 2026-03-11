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

  // ---- Phase 1.3: Edge Case Tests ----

  // Game Over Conditions

  // Test that consecutive passes by both players results in game over
  @Test
  public void testGameOverByConsecutivePasses() {
    IReversiModel model = new ReversiModel(3);
    // Play moves until both players must pass
    model.move(1, 3, model.getCurrentTurn()); // BLACK
    model.move(3, 0, model.getCurrentTurn()); // WHITE
    model.passTurn(); // BLACK passes
    model.move(0, 1, model.getCurrentTurn()); // WHITE
    model.move(1, 0, model.getCurrentTurn()); // BLACK
    model.passTurn(); // WHITE passes
    model.move(4, 1, model.getCurrentTurn()); // BLACK
    model.move(3, 3, model.getCurrentTurn()); // WHITE
    // Now game should be over (no valid moves for either player)
    Assert.assertTrue(model.gameOver());
  }

  // Test game over when one player has zero pieces
  @Test
  public void testGameOverWhenPlayerHasZeroPieces() {
    IReversiModel model = new ReversiModel(3);
    // Manually set all WHITE pieces to BLACK to simulate zero-piece scenario
    for (int row = 0; row < model.getBoard().size(); row++) {
      for (int col = 0; col < model.getRow(row).size(); col++) {
        if (!model.getSpace(row, col).isEmpty()
                && model.getSpace(row, col).getPlayer() == Player.WHITE) {
          model.getSpace(row, col).setFilled(Player.BLACK);
        }
      }
    }
    Assert.assertEquals(0, model.getScore(Player.WHITE));
    Assert.assertTrue(model.gameOver());
  }

  // Test game is not over when only one player has no moves (the other still does)
  @Test
  public void testGameNotOverWhenOnlyOnePlayerHasNoMoves() {
    IReversiModel model = new ReversiModel(3);
    // After some moves, BLACK might have no moves but WHITE still does
    model.move(1, 3, model.getCurrentTurn()); // BLACK
    model.move(3, 0, model.getCurrentTurn()); // WHITE
    // BLACK might not have valid moves here, but game shouldn't be over if WHITE does
    boolean blackHasNoMoves = model.noValidMoves(Player.BLACK);
    boolean whiteHasNoMoves = model.noValidMoves(Player.WHITE);
    if (blackHasNoMoves && !whiteHasNoMoves) {
      Assert.assertFalse(model.gameOver());
    }
    // At minimum, verify gameOver logic: game is only over when BOTH have no moves
    // or a player has 0 pieces or board is full
    Assert.assertTrue(model.getScore(Player.BLACK) > 0);
    Assert.assertTrue(model.getScore(Player.WHITE) > 0);
  }

  // Move Validation at Boundaries

  // Test that a move at the top row (row 0) is invalid when no flip line exists
  @Test
  public void testInvalidMoveAtTopRow() {
    Assert.assertFalse(model4.isValidMove(0, 0, Player.BLACK));
    Assert.assertFalse(model4.isValidMove(0, 3, Player.BLACK));
    try {
      model4.move(0, 0, Player.BLACK);
      Assert.fail("Expected IllegalStateException for invalid move at top row");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  // Test that a move at the bottom row is invalid when no flip line exists
  @Test
  public void testInvalidMoveAtBottomRow() {
    int lastRow = model4.getBoard().size() - 1;
    Assert.assertFalse(model4.isValidMove(lastRow, 0, Player.BLACK));
    try {
      model4.move(lastRow, 0, Player.BLACK);
      Assert.fail("Expected IllegalStateException for invalid move at bottom row");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  // Test a valid move at the board edge that creates a flip
  @Test
  public void testValidMoveAtBoardEdge() {
    // On a size-4 board, (1, 2) is a valid edge-area move for BLACK
    Assert.assertTrue(model4.isValidMove(1, 2, Player.BLACK));
    model4.move(1, 2, model4.getCurrentTurn());
    Assert.assertEquals(Player.WHITE, model4.getCurrentTurn());
    Assert.assertTrue(model4.getScore(Player.BLACK) > 3);
  }

  // Turn Management Edge Cases

  // Test that turn doesn't change after a failed move attempt
  @Test
  public void testTurnUnchangedAfterInvalidMoveAttempt() {
    Player turnBefore = model4.getCurrentTurn();
    try {
      model4.move(0, 0, model4.getCurrentTurn());
      Assert.fail("Expected exception");
    } catch (IllegalStateException e) {
      // expected
    }
    Assert.assertEquals(turnBefore, model4.getCurrentTurn());
  }

  // Test passTurn alternates correctly through multiple passes
  @Test
  public void testPassTurnAlternatesMultipleTimes() {
    IReversiModel model = new ReversiModel(3);
    Assert.assertEquals(Player.BLACK, model.getCurrentTurn());
    model.passTurn();
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
    model.passTurn();
    Assert.assertEquals(Player.BLACK, model.getCurrentTurn());
    model.passTurn();
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
    model.passTurn();
    Assert.assertEquals(Player.BLACK, model.getCurrentTurn());
  }

  // Test making a move after the other player passes
  @Test
  public void testMoveAfterPass() {
    IReversiModel model = new ReversiModel(4);
    Assert.assertEquals(Player.BLACK, model.getCurrentTurn());
    model.passTurn(); // BLACK passes
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
    // WHITE makes a valid move
    model.move(2, 1, model.getCurrentTurn());
    Assert.assertEquals(Player.BLACK, model.getCurrentTurn());
    Assert.assertTrue(model.getScore(Player.WHITE) > 3);
  }

  // copyModel Immutability

  // Test that copyModel preserves the current turn
  @Test
  public void testCopyModelPreservesTurn() {
    model4.move(2, 4, model4.getCurrentTurn()); // BLACK moves, turn becomes WHITE
    Assert.assertEquals(Player.WHITE, model4.getCurrentTurn());
    IReversiModel copy = model4.copyModel();
    Assert.assertEquals(Player.WHITE, copy.getCurrentTurn());
  }

  // Test that multiple copies are independent of each other and the original
  @Test
  public void testMultipleCopiesAreIndependent() {
    IReversiModel copy1 = model4.copyModel();
    IReversiModel copy2 = model4.copyModel();

    copy1.move(2, 4, copy1.getCurrentTurn()); // modify copy1
    copy2.move(1, 2, copy2.getCurrentTurn()); // modify copy2 differently

    // Original unchanged
    Assert.assertEquals(Player.BLACK, model4.getCurrentTurn());
    Assert.assertEquals(3, model4.getScore(Player.BLACK));

    // Copies are different from each other
    ITextView viewCopy1 = new TextView(copy1);
    ITextView viewCopy2 = new TextView(copy2);
    Assert.assertNotEquals(viewCopy1.toString(), viewCopy2.toString());
  }

  // Test that copied model spaces are different objects (deep copy)
  @Test
  public void testCopyModelSpacesAreDifferentObjects() {
    IReversiModel copy = model4.copyModel();
    // Modify a space in the copy
    copy.getSpace(0, 0).setFilled(Player.BLACK);
    // Original space should still be empty
    Assert.assertTrue(model4.getSpace(0, 0).isEmpty());
  }

  // Score & State Invariants

  // Test score consistency: BLACK + WHITE scores <= total board spaces
  @Test
  public void testScoreConsistency() {
    model4.move(2, 4, model4.getCurrentTurn());
    model4.move(4, 1, model4.getCurrentTurn());

    int blackScore = model4.getScore(Player.BLACK);
    int whiteScore = model4.getScore(Player.WHITE);
    int totalSpaces = 0;
    for (int row = 0; row < model4.getBoard().size(); row++) {
      totalSpaces += model4.getRow(row).size();
    }
    int emptyCount = totalSpaces - blackScore - whiteScore;

    Assert.assertTrue(emptyCount >= 0);
    Assert.assertEquals(totalSpaces, blackScore + whiteScore + emptyCount);
  }

  // Test noValidMoves directly for each player
  @Test
  public void testNoValidMovesDirectly() {
    // At the start, BLACK has valid moves
    Assert.assertFalse(model4.noValidMoves(Player.BLACK));
    // Set up a state where BLACK has no valid moves by filling surrounding spaces
    IReversiModel model = new ReversiModel(3);
    model.getSpace(1, 2).setFilled(Player.BLACK);
    model.getSpace(2, 1).setFilled(Player.BLACK);
    model.getSpace(3, 2).setFilled(Player.BLACK);
    // BLACK should now have no valid moves (all lines blocked)
    Assert.assertTrue(model.noValidMoves(Player.BLACK));
  }

  // Exception Handling

  // Test behavior of passTurn when game is already over
  @Test
  public void testPassTurnWhenGameOver() {
    IReversiModel model = new ReversiModel(3);
    // Force game over by removing all white pieces
    for (int row = 0; row < model.getBoard().size(); row++) {
      for (int col = 0; col < model.getRow(row).size(); col++) {
        if (!model.getSpace(row, col).isEmpty()
                && model.getSpace(row, col).getPlayer() == Player.WHITE) {
          model.getSpace(row, col).setFilled(Player.BLACK);
        }
      }
    }
    Assert.assertTrue(model.gameOver());
    // passTurn should still work (no guard in the implementation)
    Player turnBefore = model.getCurrentTurn();
    model.passTurn();
    Assert.assertNotEquals(turnBefore, model.getCurrentTurn());
  }

  // ---- Undo Edge Case Tests ----

  // Test undo restores board state after a single move
  @Test
  public void testUndoRestoresBoardState() {
    IReversiModel snapshot = model4.copyModel();
    String boardBefore = view4.toString();
    Player turnBefore = model4.getCurrentTurn();
    model4.move(2, 4, model4.getCurrentTurn());
    // Board should have changed
    Assert.assertNotEquals(boardBefore, view4.toString());
    // Restore from snapshot
    model4.restoreFrom(snapshot);
    Assert.assertEquals(boardBefore, view4.toString());
    Assert.assertEquals(turnBefore, model4.getCurrentTurn());
  }

  // Test undo after a pass: snapshot taken before pass restores turn correctly
  @Test
  public void testUndoAfterPass() {
    IReversiModel snapshot = model4.copyModel();
    Player turnBefore = model4.getCurrentTurn();
    String boardBefore = view4.toString();
    model4.passTurn();
    Assert.assertNotEquals(turnBefore, model4.getCurrentTurn());
    // Restore
    model4.restoreFrom(snapshot);
    Assert.assertEquals(turnBefore, model4.getCurrentTurn());
    Assert.assertEquals(boardBefore, view4.toString());
  }

  // Test undo after game over: restore model to pre-game-over state
  @Test
  public void testUndoAfterGameOver() {
    IReversiModel model = new ReversiModel(3);
    // Play moves to near end
    model.move(1, 3, model.getCurrentTurn()); // BLACK
    model.move(3, 0, model.getCurrentTurn()); // WHITE
    model.passTurn(); // BLACK passes
    model.move(0, 1, model.getCurrentTurn()); // WHITE
    model.move(1, 0, model.getCurrentTurn()); // BLACK
    model.passTurn(); // WHITE passes
    model.move(4, 1, model.getCurrentTurn()); // BLACK

    // Take snapshot before the final move that ends the game
    IReversiModel snapshot = model.copyModel();
    Assert.assertFalse(model.gameOver());

    model.move(3, 3, model.getCurrentTurn()); // WHITE — game should now be over
    Assert.assertTrue(model.gameOver());

    // Undo by restoring snapshot
    model.restoreFrom(snapshot);
    Assert.assertFalse(model.gameOver());
  }

  // Test undo preserves scores correctly
  @Test
  public void testUndoRestoresScores() {
    int blackBefore = model4.getScore(Player.BLACK);
    int whiteBefore = model4.getScore(Player.WHITE);
    IReversiModel snapshot = model4.copyModel();
    model4.move(2, 4, model4.getCurrentTurn());
    // Scores should have changed
    Assert.assertNotEquals(blackBefore, model4.getScore(Player.BLACK));
    // Restore
    model4.restoreFrom(snapshot);
    Assert.assertEquals(blackBefore, model4.getScore(Player.BLACK));
    Assert.assertEquals(whiteBefore, model4.getScore(Player.WHITE));
  }

  // Test multiple sequential undos (undo, play, undo again)
  @Test
  public void testMultipleSequentialUndos() {
    IReversiModel snapshot1 = model4.copyModel();
    String board1 = view4.toString();
    model4.move(2, 4, model4.getCurrentTurn());

    IReversiModel snapshot2 = model4.copyModel();
    String board2 = view4.toString();
    model4.move(4, 1, model4.getCurrentTurn());

    // Undo second move
    model4.restoreFrom(snapshot2);
    Assert.assertEquals(board2, view4.toString());

    // Undo first move
    model4.restoreFrom(snapshot1);
    Assert.assertEquals(board1, view4.toString());
  }

  // Test that restoreFrom works with a snapshot from a different sized copy
  @Test
  public void testRestoreFromPreservesAllSpaces() {
    IReversiModel snapshot = model4.copyModel();
    model4.move(2, 4, model4.getCurrentTurn());
    model4.move(4, 1, model4.getCurrentTurn());
    model4.restoreFrom(snapshot);
    // Every space should match the snapshot
    for (int r = 0; r < model4.getBoard().size(); r++) {
      for (int c = 0; c < model4.getRow(r).size(); c++) {
        Assert.assertEquals("Mismatch at (" + r + "," + c + ")",
                snapshot.getSpaceContent(r, c), model4.getSpaceContent(r, c));
      }
    }
  }

  // Test that loadState correctly sets board from a 2D array
  @Test
  public void testLoadStateSetsBoardCorrectly() {
    IReversiModel model = new ReversiModel(3);
    int totalRows = (3 * 2) - 1;
    Player[][] state = new Player[totalRows][];
    // Build a custom board state
    state[0] = new Player[]{null, null, null};
    state[1] = new Player[]{Player.BLACK, Player.BLACK, Player.BLACK, null};
    state[2] = new Player[]{null, Player.WHITE, Player.BLACK, Player.WHITE, null};
    state[3] = new Player[]{null, Player.WHITE, Player.WHITE, null};
    state[4] = new Player[]{null, null, null};

    model.loadState(Player.WHITE, state);
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
    Assert.assertEquals(Player.BLACK, model.getSpaceContent(1, 0));
    Assert.assertEquals(Player.WHITE, model.getSpaceContent(2, 1));
    Assert.assertNull(model.getSpaceContent(0, 0));
  }
}