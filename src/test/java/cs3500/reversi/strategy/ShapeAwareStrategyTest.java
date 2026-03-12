package cs3500.reversi.strategy;

import org.junit.Assert;
import org.junit.Test;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.model.SquareBoardShape;
import cs3500.reversi.model.HexBoardShape;

/**
 * Tests for shape-aware strategies on different board geometries.
 */
public class ShapeAwareStrategyTest {

  // ==================== ShapeAwareCornersFirst on Square ====================

  @Test
  public void testShapeAwareCornersFirstOnSquareDoesNotCrash() {
    IReversiModel model = new ReversiModel(8, new SquareBoardShape());
    ShapeAwareCornersFirst strategy = new ShapeAwareCornersFirst();
    // Should be able to make a move without error
    strategy.chooseNextMove(model, Player.BLACK);
    // Turn should have advanced (either move or pass)
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
  }

  @Test
  public void testShapeAwareCornersFirstOnHex() {
    IReversiModel model = new ReversiModel(4, new HexBoardShape());
    ShapeAwareCornersFirst strategy = new ShapeAwareCornersFirst();
    strategy.chooseNextMove(model, Player.BLACK);
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
  }

  // ==================== ShapeAwareAvoidNextToCorners on Square ====================

  @Test
  public void testShapeAwareAvoidNextToCornersOnSquare() {
    IReversiModel model = new ReversiModel(8, new SquareBoardShape());
    ShapeAwareAvoidNextToCorners strategy = new ShapeAwareAvoidNextToCorners();
    strategy.chooseNextMove(model, Player.BLACK);
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
  }

  @Test
  public void testShapeAwareAvoidNextToCornersOnHex() {
    IReversiModel model = new ReversiModel(4, new HexBoardShape());
    ShapeAwareAvoidNextToCorners strategy = new ShapeAwareAvoidNextToCorners();
    strategy.chooseNextMove(model, Player.BLACK);
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
  }

  // ==================== Generic Strategies on Square ====================

  @Test
  public void testAsManyPiecesOnSquare() {
    IReversiModel model = new ReversiModel(8, new SquareBoardShape());
    AsManyPiecesAsPossible strategy = new AsManyPiecesAsPossible();
    strategy.chooseNextMove(model, Player.BLACK);
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
    // Score should have increased
    Assert.assertTrue(model.getScore(Player.BLACK) > 2);
  }

  @Test
  public void testMiniMaxOnSquare() {
    IReversiModel model = new ReversiModel(4, new SquareBoardShape());
    MiniMax strategy = new MiniMax();
    strategy.chooseNextMove(model, Player.BLACK);
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
  }

  @Test
  public void testAlphaBetaOnSquare() {
    IReversiModel model = new ReversiModel(4, new SquareBoardShape());
    AlphaBetaMiniMax strategy = new AlphaBetaMiniMax(2);
    strategy.chooseNextMove(model, Player.BLACK);
    Assert.assertEquals(Player.WHITE, model.getCurrentTurn());
  }

  // ==================== Full AI Game on Square ====================

  @Test
  public void testFullAIGameOnSquare() {
    IReversiModel model = new ReversiModel(4, new SquareBoardShape());
    AsManyPiecesAsPossible blackStrategy = new AsManyPiecesAsPossible();
    ShapeAwareCornersFirst whiteStrategy = new ShapeAwareCornersFirst();

    int maxTurns = 100;
    int turns = 0;
    while (!model.gameOver() && turns < maxTurns) {
      if (model.getCurrentTurn() == Player.BLACK) {
        blackStrategy.chooseNextMove(model, Player.BLACK);
      } else {
        whiteStrategy.chooseNextMove(model, Player.WHITE);
      }
      turns++;
    }
    Assert.assertTrue("Game should finish within " + maxTurns + " turns", model.gameOver());
    Assert.assertTrue(model.getScore(Player.BLACK) + model.getScore(Player.WHITE) > 4);
  }

  // ==================== Full AI Game on Hex (Backward Compat) ====================

  @Test
  public void testFullAIGameOnHex() {
    IReversiModel model = new ReversiModel(3, new HexBoardShape());
    ShapeAwareCornersFirst blackStrategy = new ShapeAwareCornersFirst();
    ShapeAwareAvoidNextToCorners whiteStrategy = new ShapeAwareAvoidNextToCorners();

    int maxTurns = 100;
    int turns = 0;
    while (!model.gameOver() && turns < maxTurns) {
      if (model.getCurrentTurn() == Player.BLACK) {
        blackStrategy.chooseNextMove(model, Player.BLACK);
      } else {
        whiteStrategy.chooseNextMove(model, Player.WHITE);
      }
      turns++;
    }
    Assert.assertTrue("Game should finish within " + maxTurns + " turns", model.gameOver());
  }

  @Test
  public void testFullAIGameOnSquare8x8() {
    IReversiModel model = new ReversiModel(8, new SquareBoardShape());
    AlphaBetaMiniMax blackStrategy = new AlphaBetaMiniMax(2);
    AsManyPiecesAsPossible whiteStrategy = new AsManyPiecesAsPossible();

    int maxTurns = 200;
    int turns = 0;
    while (!model.gameOver() && turns < maxTurns) {
      if (model.getCurrentTurn() == Player.BLACK) {
        blackStrategy.chooseNextMove(model, Player.BLACK);
      } else {
        whiteStrategy.chooseNextMove(model, Player.WHITE);
      }
      turns++;
    }
    Assert.assertTrue("Game should finish", model.gameOver());
  }
}
