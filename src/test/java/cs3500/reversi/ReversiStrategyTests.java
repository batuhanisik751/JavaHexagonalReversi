package cs3500.reversi;

import org.junit.Assert;
import org.junit.Test;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.view.ITextView;
import cs3500.reversi.view.TextView;
import cs3500.reversi.strategy.AsManyPiecesAsPossible;
import cs3500.reversi.strategy.AlphaBetaMiniMax;
import cs3500.reversi.strategy.AvoidNextToCorners;
import cs3500.reversi.strategy.CornersFirst;
import cs3500.reversi.strategy.DeepMiniMax;
import cs3500.reversi.strategy.IReversiStrategies;
import cs3500.reversi.strategy.MiniMax;

/**
 * Tests for the IReversiStrategies.
 */
public class ReversiStrategyTests {
  IReversiModel model3 = new ReversiModel(3);
  ITextView view3 = new TextView(model3);
  IReversiModel model4 = new ReversiModel(4);
  ITextView view4 = new TextView(model4);


  // AsManyPiecesAsPossible Tests

  // Test that an AsManyPiecesAsPossible picks the left-most and up-most move in a tie.
  @Test
  public void testLeftUpperMostSpaceInTie() {
    IReversiStrategies asManyPiecesAsPossible = new AsManyPiecesAsPossible();
    asManyPiecesAsPossible.chooseNextMove(model4, model4.getCurrentTurn());
    Assert.assertEquals("    _ _ _ _     \n" +
            "   _ _ X _ _    \n" +
            "  _ _ X X _ _   \n" +
            " _ _ O _ X _ _  \n" +
            "  _ _ X O _ _   \n" +
            "   _ _ _ _ _    \n" +
            "    _ _ _ _     \n", view4.toString());
  }

  // Test AsManyPieceAsPossible passes and no move is made if there are no available turns
  @Test
  public void testAsManyPiecesAsPossiblePassesTurn() {
    IReversiStrategies asManyPiecesAsPossible = new AsManyPiecesAsPossible();
    model3.passTurn();
    model3.move(3, 0, model3.getCurrentTurn());
    model3.passTurn();
    model3.move(0, 1, model3.getCurrentTurn());
    asManyPiecesAsPossible.chooseNextMove(model3, model3.getCurrentTurn());
    asManyPiecesAsPossible.chooseNextMove(model3, model3.getCurrentTurn());
    Assert.assertEquals("   _ O _    \n" +
            "  _ O O _   \n" +
            " _ O _ X _  \n" +
            "  O O O O   \n" +
            "   _ X _    \n", view3.toString());
    asManyPiecesAsPossible.chooseNextMove(model3, model3.getCurrentTurn());
    Assert.assertEquals(Player.WHITE, model3.getCurrentTurn());
    Assert.assertEquals("   _ O _    \n" +
            "  _ O O _   \n" +
            " _ O _ X _  \n" +
            "  O O O O   \n" +
            "   _ X _    \n", view3.toString());
  }

  // Test that AsManyAsPossible checks each space and finds the valid moves
  @Test
  public void testMockAsManyAsPossible() {
    StringBuilder mockOutput = new StringBuilder();
    MockAsManyAsPossible mockStrategies = new MockAsManyAsPossible(mockOutput);
    mockStrategies.chooseNextMove(model4, model4.getCurrentTurn());
    Assert.assertTrue(mockOutput.toString().contains("testing move : row = 0, col = 0")
            && mockOutput.toString().contains("testing move : row = 0, col = 1")
            && mockOutput.toString().contains("testing move : row = 1, col = 2")
            && mockOutput.toString().contains("found valid move : row = 1, col = 2, score = 5")
            && mockOutput.toString().contains("testing move : row = 5, col = 2")
            && mockOutput.toString().contains("found valid move : row = 5, col = 2, score = 5")
            && mockOutput.toString().contains("Best move : row = 1, col = 2"));
  }


  // AvoidNextToCorner Tests

  // Test AvoidNextToCorners makes changes to the model
  @Test
  public void testAvoidNextToCornersWorksWithModel() {
    IReversiStrategies avoidNextToCorners = new AvoidNextToCorners();
    avoidNextToCorners.chooseNextMove(model4, model4.getCurrentTurn());
    Assert.assertEquals("    _ _ _ _     \n" +
            "   _ _ X _ _    \n" +
            "  _ _ X X _ _   \n" +
            " _ _ O _ X _ _  \n" +
            "  _ _ X O _ _   \n" +
            "   _ _ _ _ _    \n" +
            "    _ _ _ _     \n", view4.toString());
    avoidNextToCorners.chooseNextMove(model4, model4.getCurrentTurn());
    Assert.assertEquals("    _ _ _ _     \n" +
            "   _ _ X _ _    \n" +
            "  _ _ X X _ _   \n" +
            " _ _ O _ X _ _  \n" +
            "  _ O O O _ _   \n" +
            "   _ _ _ _ _    \n" +
            "    _ _ _ _     \n", view4.toString());
  }

  // Test AvoidNextToCorners passes the turn within the model
  @Test
  public void testAvoidNextToCornersPassesTurn() {
    IReversiStrategies avoidNextToCorners = new AvoidNextToCorners();
    Assert.assertEquals(Player.BLACK, model3.getCurrentTurn());
    avoidNextToCorners.chooseNextMove(model3, model3.getCurrentTurn());
    Assert.assertEquals(Player.WHITE, model3.getCurrentTurn());
  }

  // Test that an AvoidNextToCorners, finds a valid Space next to a corner, but ignores it
  // and picks the next best move
  @Test
  public void testMockAvoidCornersIgnoresValidNextToCornerMove() {
    StringBuilder mockOutput = new StringBuilder();
    MockAvoidCorners mockAvoidCorners = new MockAvoidCorners(mockOutput);
    model4.move(1, 2, model4.getCurrentTurn());
    mockAvoidCorners.chooseNextMove(model4, model4.getCurrentTurn());
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move next to corner : row = 0, col = 2"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move next to corner : row = 1, col = 4"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move next to corner : row = 2, col = 5"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move not next to corner : row = 4, col = 1"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move not next to corner : row = 5, col = 2"));
    Assert.assertTrue(mockOutput.toString()
            .contains("Best move found : row = 4, col = 1"));
  }

  // Test that AvoidNextToCorners picks the upper left-most Space in a tie.
  @Test
  public void testMockAvoidNextToCornersTie() {
    StringBuilder mockOutput = new StringBuilder();
    MockAvoidCorners mockAvoidCorners = new MockAvoidCorners(mockOutput);
    mockAvoidCorners.chooseNextMove(model4, model4.getCurrentTurn());
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move not next to corner : row = 1, col = 2"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move not next to corner : row = 2, col = 4"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move not next to corner : row = 4, col = 4"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move not next to corner : row = 5, col = 2"));
    Assert.assertTrue(mockOutput.toString()
            .contains("Best move found : row = 1, col = 2"));
  }

  // Test that the turn is passed if there are valid moves, but they are next to corners
  @Test
  public void testMockAvoidCornersValidMovesButNextToCorners() {
    StringBuilder mockOutput = new StringBuilder();
    MockAvoidCorners mockAvoidCorners = new MockAvoidCorners(mockOutput);
    mockAvoidCorners.chooseNextMove(model3, model3.getCurrentTurn());
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move next to corner : row = 0, col = 1"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move next to corner : row = 1, col = 3"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move next to corner : row = 3, col = 3"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move next to corner : row = 4, col = 1"));
    Assert.assertTrue(mockOutput.toString()
            .contains("No valid moves not next to corners. Passing turn."));
  }


  // CornersFirst Tests

  // Test a CornersFirst makes changes (move and pass) within the model
  @Test
  public void testCornersFirstMakesMoveInModel() {
    IReversiStrategies cornersFirst = new CornersFirst();
    model3.getSpace(2, 2).setFilled(Player.BLACK);
    Assert.assertEquals("   _ _ _    \n" +
            "  _ X O _   \n" +
            " _ O X X _  \n" +
            "  _ X O _   \n" +
            "   _ _ _    \n", view3.toString());
    Assert.assertEquals(Player.BLACK, model3.getCurrentTurn());
    cornersFirst.chooseNextMove(model3, model3.getCurrentTurn());
    Assert.assertEquals(Player.WHITE, model3.getCurrentTurn());
    Assert.assertEquals("   _ _ X    \n" +
            "  _ X X _   \n" +
            " _ O X X _  \n" +
            "  _ X O _   \n" +
            "   _ _ _    \n", view3.toString());
  }

  // Test a CornersFirst finds valid moves, but prioritizes corner Spaces and picks
  // the upper left-most space in a tie
  @Test
  public void testCornersFirstPrioritizesCornerSpaces() {
    StringBuilder mockOutput = new StringBuilder();
    MockCornersFirst mockCornersFirst = new MockCornersFirst(mockOutput);
    model3.getSpace(2, 2).setFilled(Player.BLACK);
    mockCornersFirst.chooseNextMove(model3, model3.getCurrentTurn());
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (non-corner) move : row = 0, col = 1"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (corner) move : row = 0, col = 2 "));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (non-corner) move : row = 1, col = 3"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (corner) move : row = 2, col = 0"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (non-corner) move : row = 3, col = 3"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (non-corner) move : row = 4, col = 1"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (corner) move : row = 4, col = 2"));
    Assert.assertTrue(mockOutput.toString()
            .contains("Best move found : row = 0, col = 2"));
  }


  // Test that a turn is passed if there are valid moves, but none in a corner
  @Test
  public void testCornersFirstPassesNoCornerMoves() {
    StringBuilder mockOutput = new StringBuilder();
    MockCornersFirst mockCornersFirst = new MockCornersFirst(mockOutput);
    mockCornersFirst.chooseNextMove(model4, model4.getCurrentTurn());
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (non-corner) move : row = 1, col = 2"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (non-corner) move : row = 2, col = 4"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (non-corner) move : row = 4, col = 4"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid (non-corner) move : row = 5, col = 2"));
    Assert.assertTrue(mockOutput.toString()
            .contains("No valid moves at corners. Passing turn."));
  }



  // MiniMax Tests

  // Test that a MiniMax makes changes (moves and passes) in the model
  @Test
  public void testMiniMaxChangesModel() {
    IReversiStrategies miniMax = new MiniMax();
    model3.move(1, 3, model3.getCurrentTurn());
    Assert.assertEquals("   _ _ _    \n" +
            "  _ X X X   \n" +
            " _ O _ X _  \n" +
            "  _ X O _   \n" +
            "   _ _ _    \n", view3.toString());
    Assert.assertEquals(Player.WHITE, model3.getCurrentTurn());
    miniMax.chooseNextMove(model3, model3.getCurrentTurn());
    Assert.assertEquals(Player.BLACK, model3.getCurrentTurn());
    Assert.assertEquals("   _ O _    \n" +
            "  _ O X X   \n" +
            " _ O _ X _  \n" +
            "  _ X O _   \n" +
            "   _ _ _    \n", view3.toString());
  }

  // Test a MiniMax finds valid moves, but picks prioritizes Spaces that would lower the
  // opponents max scoring moves next turn and picks the upper left-most space in a tie
  @Test
  public void testMiniMaxPrioritizesMinimumScoreForOpponent() {
    StringBuilder mockOutput = new StringBuilder();
    MockMiniMax mockMiniMax = new MockMiniMax(mockOutput);
    model3.move(1, 3, model3.getCurrentTurn());
    mockMiniMax.chooseNextMove(model3, model3.getCurrentTurn());
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move : row = 0, col = 1, max opponent score = 6"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move : row = 3, col = 0, max opponent score = 8"));
    Assert.assertTrue(mockOutput.toString()
            .contains("found valid move : row = 4, col = 1, max opponent score = 6"));
    Assert.assertTrue(mockOutput.toString()
            .contains("Best move : row = 0, col = 1"));
  }

  // Test that a turn is passed if there are valid moves
  @Test
  public void testMiniMaxPassesTurnWithNoValid() {
    StringBuilder mockOutput = new StringBuilder();
    MockMiniMax mockMiniMax = new MockMiniMax(mockOutput);
    model3.getSpace(1, 2).setFilled(Player.BLACK);
    model3.getSpace(2, 1).setFilled(Player.BLACK);
    model3.getSpace(3, 2).setFilled(Player.BLACK);
    mockMiniMax.chooseNextMove(model3, model3.getCurrentTurn());
    Assert.assertTrue(mockOutput.toString().contains("No valid moves found. Passing turn."));
  }

  // ---- Phase 1.3: Strategy Edge Case Tests ----

  // Test AsManyPiecesAsPossible works on a board size 5
  @Test
  public void testAsManyPiecesAsPossibleOnBoardSize5() {
    IReversiModel model5 = new ReversiModel(5);
    IReversiStrategies strategy = new AsManyPiecesAsPossible();
    Player turnBefore = model5.getCurrentTurn();
    strategy.chooseNextMove(model5, model5.getCurrentTurn());
    // Should have made a move (turn changed)
    Assert.assertNotEquals(turnBefore, model5.getCurrentTurn());
    Assert.assertTrue(model5.getScore(Player.BLACK) > 3);
  }

  // Test MiniMax works on a board size 5
  @Test
  public void testMiniMaxOnBoardSize5() {
    IReversiModel model5 = new ReversiModel(5);
    IReversiStrategies strategy = new MiniMax();
    Player turnBefore = model5.getCurrentTurn();
    strategy.chooseNextMove(model5, model5.getCurrentTurn());
    Assert.assertNotEquals(turnBefore, model5.getCurrentTurn());
  }

  // Test all strategies produce valid moves in a mid-game state
  @Test
  public void testStrategiesAfterSeveralMoves() {
    IReversiStrategies[] strategies = {
            new AsManyPiecesAsPossible(),
            new AvoidNextToCorners(),
            new CornersFirst(),
            new MiniMax()
    };
    for (IReversiStrategies strategy : strategies) {
      IReversiModel model = new ReversiModel(4);
      // Play a few moves to get to mid-game
      model.move(2, 4, model.getCurrentTurn());
      model.move(4, 1, model.getCurrentTurn());
      // Now let the strategy play for BLACK
      int scoreBefore = model.getScore(Player.BLACK) + model.getScore(Player.WHITE);
      strategy.chooseNextMove(model, model.getCurrentTurn());
      // Strategy should either make a move (score increases) or pass (turn changes)
      int scoreAfter = model.getScore(Player.BLACK) + model.getScore(Player.WHITE);
      Assert.assertTrue(scoreAfter >= scoreBefore);
    }
  }

  // Test CornersFirst tie-breaking with multiple corner moves
  @Test
  public void testCornersFirstTieBreaking() {
    IReversiModel model = new ReversiModel(3);
    // Set up board so multiple corners are valid moves
    model.getSpace(2, 2).setFilled(Player.BLACK);
    StringBuilder mockOutput = new StringBuilder();
    MockCornersFirst mockCornersFirst = new MockCornersFirst(mockOutput);
    mockCornersFirst.chooseNextMove(model, model.getCurrentTurn());
    // Should pick the upper-left-most corner
    String output = mockOutput.toString();
    Assert.assertTrue(output.contains("Best move found"));
    // The best corner move should be the topmost/leftmost one
    Assert.assertTrue(output.contains("Best move found : row = 0, col = 2"));
  }

  // ---- CornersFirst Fallback Bug Tests ----

  // CornersFirst should fall back to the upper-left-most valid move when no corners are available,
  // NOT pass the turn. The Javadoc states: "if there are no corners available then the upper
  // left-most valid move will be made."
  @Test
  public void testCornersFirstFallsBackToNonCornerMove() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies cornersFirst = new CornersFirst();
    Player turnBefore = model.getCurrentTurn();
    int scoreBefore = model.getScore(turnBefore);
    cornersFirst.chooseNextMove(model, turnBefore);
    // On a size-4 board at the start, no corners are valid moves.
    // The strategy should still make a move, not pass.
    Assert.assertNotEquals("CornersFirst should make a move even when no corners are available",
            turnBefore, model.getCurrentTurn());
    Assert.assertTrue("CornersFirst should have placed a piece",
            model.getScore(turnBefore) > scoreBefore);
  }

  // CornersFirst should not pass when valid non-corner moves exist
  @Test
  public void testCornersFirstDoesNotPassWithValidMoves() {
    IReversiModel model = new ReversiModel(5);
    IReversiStrategies cornersFirst = new CornersFirst();
    Player turnBefore = model.getCurrentTurn();
    int scoreBefore = model.getScore(turnBefore);
    cornersFirst.chooseNextMove(model, turnBefore);
    // Should have made a move (turn changes and score increases)
    Assert.assertNotEquals(turnBefore, model.getCurrentTurn());
    Assert.assertTrue("CornersFirst should place a piece when valid moves exist",
            model.getScore(turnBefore) > scoreBefore);
  }

  // ---- AvoidNextToCorners Fallback Bug Tests ----

  // AvoidNextToCorners should fall back to a corner-adjacent move when ALL valid moves
  // are next to corners, NOT pass the turn. The Javadoc states: "If there are only Spaces
  // next to corners available as valid moves, the upper left-most will be chosen."
  @Test
  public void testAvoidNextToCornersFallsBackToCornerAdjacentMove() {
    IReversiModel model = new ReversiModel(3);
    IReversiStrategies avoid = new AvoidNextToCorners();
    Player turnBefore = model.getCurrentTurn();
    int scoreBefore = model.getScore(turnBefore);
    avoid.chooseNextMove(model, turnBefore);
    // On a size-3 board, all valid moves are next to corners.
    // The strategy should still make a move, not pass.
    Assert.assertTrue("AvoidNextToCorners should place a piece even when all moves "
                    + "are next to corners",
            model.getScore(turnBefore) > scoreBefore);
  }

  // AvoidNextToCorners should make a move on small boards where all moves are corner-adjacent
  @Test
  public void testAvoidNextToCornersDoesNotPassOnSmallBoard() {
    IReversiModel model = new ReversiModel(3);
    IReversiStrategies avoid = new AvoidNextToCorners();
    Player turnBefore = model.getCurrentTurn();
    avoid.chooseNextMove(model, turnBefore);
    // Turn should change because a move was made, not just a pass
    Assert.assertNotEquals(turnBefore, model.getCurrentTurn());
    // The total piece count should have increased (a piece was placed and flips happened)
    int totalPieces = model.getScore(Player.BLACK) + model.getScore(Player.WHITE);
    Assert.assertTrue("A move should increase total pieces on board", totalPieces > 6);
  }

  // ---- DeepMiniMax Tests ----

  // Test DeepMiniMax makes a valid move at depth 1
  @Test
  public void testDeepMiniMaxDepth1MakesMove() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies strategy = new DeepMiniMax(1);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    Assert.assertNotEquals("DeepMiniMax depth 1 should make a move",
            turnBefore, model.getCurrentTurn());
  }

  // Test DeepMiniMax makes a valid move at depth 2
  @Test
  public void testDeepMiniMaxDepth2MakesMove() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies strategy = new DeepMiniMax(2);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    Assert.assertNotEquals("DeepMiniMax depth 2 should make a move",
            turnBefore, model.getCurrentTurn());
  }

  // Test DeepMiniMax makes a valid move at depth 3
  @Test
  public void testDeepMiniMaxDepth3MakesMove() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies strategy = new DeepMiniMax(3);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    Assert.assertNotEquals("DeepMiniMax depth 3 should make a move",
            turnBefore, model.getCurrentTurn());
    Assert.assertTrue(model.getScore(turnBefore) > 3);
  }

  // Test DeepMiniMax passes when no valid moves exist
  @Test
  public void testDeepMiniMaxPassesWhenNoMoves() {
    IReversiModel model = new ReversiModel(3);
    model.getSpace(1, 2).setFilled(Player.BLACK);
    model.getSpace(2, 1).setFilled(Player.BLACK);
    model.getSpace(3, 2).setFilled(Player.BLACK);
    IReversiStrategies strategy = new DeepMiniMax(3);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    // Should pass (turn changes but no pieces placed)
    Assert.assertNotEquals(turnBefore, model.getCurrentTurn());
  }

  // Test DeepMiniMax works on board size 5
  @Test
  public void testDeepMiniMaxOnBoardSize5() {
    IReversiModel model = new ReversiModel(5);
    IReversiStrategies strategy = new DeepMiniMax(2);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    Assert.assertNotEquals(turnBefore, model.getCurrentTurn());
  }

  // Test DeepMiniMax in a mid-game state
  @Test
  public void testDeepMiniMaxMidGame() {
    IReversiModel model = new ReversiModel(4);
    model.move(2, 4, model.getCurrentTurn());
    model.move(4, 1, model.getCurrentTurn());
    IReversiStrategies strategy = new DeepMiniMax(2);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    Assert.assertNotEquals(turnBefore, model.getCurrentTurn());
  }

  // ---- AlphaBetaMiniMax Tests ----

  // Test AlphaBetaMiniMax makes a valid move at depth 1
  @Test
  public void testAlphaBetaDepth1MakesMove() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies strategy = new AlphaBetaMiniMax(1);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    Assert.assertNotEquals("AlphaBetaMiniMax depth 1 should make a move",
            turnBefore, model.getCurrentTurn());
  }

  // Test AlphaBetaMiniMax makes a valid move at depth 3 (the "hard" difficulty)
  @Test
  public void testAlphaBetaDepth3MakesMove() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies strategy = new AlphaBetaMiniMax(3);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    Assert.assertNotEquals("AlphaBetaMiniMax depth 3 should make a move",
            turnBefore, model.getCurrentTurn());
    Assert.assertTrue(model.getScore(turnBefore) > 3);
  }

  // Test AlphaBetaMiniMax passes when no valid moves exist
  @Test
  public void testAlphaBetaPassesWhenNoMoves() {
    IReversiModel model = new ReversiModel(3);
    model.getSpace(1, 2).setFilled(Player.BLACK);
    model.getSpace(2, 1).setFilled(Player.BLACK);
    model.getSpace(3, 2).setFilled(Player.BLACK);
    IReversiStrategies strategy = new AlphaBetaMiniMax(3);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    Assert.assertNotEquals(turnBefore, model.getCurrentTurn());
  }

  // Test AlphaBetaMiniMax produces same result as DeepMiniMax at same depth
  @Test
  public void testAlphaBetaMatchesDeepMiniMax() {
    IReversiModel modelAB = new ReversiModel(4);
    IReversiModel modelDeep = new ReversiModel(4);
    IReversiStrategies alphaBeta = new AlphaBetaMiniMax(3);
    IReversiStrategies deep = new DeepMiniMax(3);
    alphaBeta.chooseNextMove(modelAB, modelAB.getCurrentTurn());
    deep.chooseNextMove(modelDeep, modelDeep.getCurrentTurn());
    // Both should produce the same board state
    ITextView viewAB = new TextView(modelAB);
    ITextView viewDeep = new TextView(modelDeep);
    Assert.assertEquals("AlphaBeta and DeepMiniMax should choose the same move at depth 3",
            viewDeep.toString(), viewAB.toString());
  }

  // Test AlphaBetaMiniMax works on board size 5
  @Test
  public void testAlphaBetaOnBoardSize5() {
    IReversiModel model = new ReversiModel(5);
    IReversiStrategies strategy = new AlphaBetaMiniMax(2);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    Assert.assertNotEquals(turnBefore, model.getCurrentTurn());
  }

  // Test AlphaBetaMiniMax in a mid-game state
  @Test
  public void testAlphaBetaMidGame() {
    IReversiModel model = new ReversiModel(4);
    model.move(2, 4, model.getCurrentTurn());
    model.move(4, 1, model.getCurrentTurn());
    IReversiStrategies strategy = new AlphaBetaMiniMax(3);
    Player turnBefore = model.getCurrentTurn();
    strategy.chooseNextMove(model, turnBefore);
    Assert.assertNotEquals(turnBefore, model.getCurrentTurn());
  }

  // ---- AI vs AI Full Game Simulation Tests ----

  // Test that two AsManyPiecesAsPossible AIs can play a full game to completion
  @Test
  public void testAIvsAIFullGameEasyVsEasy() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies black = new AsManyPiecesAsPossible();
    IReversiStrategies white = new AsManyPiecesAsPossible();
    int maxTurns = 100;
    int turns = 0;
    while (!model.gameOver() && turns < maxTurns) {
      if (model.getCurrentTurn() == Player.BLACK) {
        black.chooseNextMove(model, Player.BLACK);
      } else {
        white.chooseNextMove(model, Player.WHITE);
      }
      turns++;
    }
    Assert.assertTrue("AI vs AI game should complete", model.gameOver());
    Assert.assertTrue("Game should complete in reasonable turns", turns < maxTurns);
  }

  // Test that MiniMax vs AsManyPiecesAsPossible can play a full game
  @Test
  public void testAIvsAIMiniMaxVsEasy() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies black = new MiniMax();
    IReversiStrategies white = new AsManyPiecesAsPossible();
    int maxTurns = 100;
    int turns = 0;
    while (!model.gameOver() && turns < maxTurns) {
      if (model.getCurrentTurn() == Player.BLACK) {
        black.chooseNextMove(model, Player.BLACK);
      } else {
        white.chooseNextMove(model, Player.WHITE);
      }
      turns++;
    }
    Assert.assertTrue("AI vs AI game should complete", model.gameOver());
  }

  // Test that AlphaBetaMiniMax vs AsManyPiecesAsPossible can play a full game
  @Test
  public void testAIvsAIAlphaBetaVsEasy() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies black = new AlphaBetaMiniMax(3);
    IReversiStrategies white = new AsManyPiecesAsPossible();
    int maxTurns = 100;
    int turns = 0;
    while (!model.gameOver() && turns < maxTurns) {
      if (model.getCurrentTurn() == Player.BLACK) {
        black.chooseNextMove(model, Player.BLACK);
      } else {
        white.chooseNextMove(model, Player.WHITE);
      }
      turns++;
    }
    Assert.assertTrue("AI vs AI game should complete", model.gameOver());
  }

  // Test CornersFirst vs AsManyPiecesAsPossible full game
  @Test
  public void testAIvsAICornersFirstVsEasy() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies black = new CornersFirst();
    IReversiStrategies white = new AsManyPiecesAsPossible();
    int maxTurns = 100;
    int turns = 0;
    while (!model.gameOver() && turns < maxTurns) {
      if (model.getCurrentTurn() == Player.BLACK) {
        black.chooseNextMove(model, Player.BLACK);
      } else {
        white.chooseNextMove(model, Player.WHITE);
      }
      turns++;
    }
    Assert.assertTrue("CornersFirst vs Easy game should complete", model.gameOver());
  }

  // Test AvoidNextToCorners vs AsManyPiecesAsPossible full game
  @Test
  public void testAIvsAIAvoidCornersVsEasy() {
    IReversiModel model = new ReversiModel(4);
    IReversiStrategies black = new AvoidNextToCorners();
    IReversiStrategies white = new AsManyPiecesAsPossible();
    int maxTurns = 100;
    int turns = 0;
    while (!model.gameOver() && turns < maxTurns) {
      if (model.getCurrentTurn() == Player.BLACK) {
        black.chooseNextMove(model, Player.BLACK);
      } else {
        white.chooseNextMove(model, Player.WHITE);
      }
      turns++;
    }
    Assert.assertTrue("AvoidCorners vs Easy game should complete", model.gameOver());
  }
}
