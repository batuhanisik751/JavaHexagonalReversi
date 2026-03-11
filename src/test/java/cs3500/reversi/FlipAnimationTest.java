package cs3500.reversi;

import org.junit.Test;

import java.util.List;

import cs3500.reversi.controller.AIPlayer;
import cs3500.reversi.controller.Controller;
import cs3500.reversi.controller.HumanPlayer;
import cs3500.reversi.controller.ViewListener;
import cs3500.reversi.history.GameHistory;
import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.strategy.AsManyPiecesAsPossible;
import cs3500.reversi.view.FlipAnimationUtils;
import cs3500.reversi.view.IGraphicsView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the piece flip animation feature.
 */
public class FlipAnimationTest {

  @Test
  public void testComputeFlipScaleXAtStart() {
    assertEquals(1.0, FlipAnimationUtils.computeFlipScaleX(0.0), 0.001);
  }

  @Test
  public void testComputeFlipScaleXAtQuarter() {
    assertEquals(0.5, FlipAnimationUtils.computeFlipScaleX(0.25), 0.001);
  }

  @Test
  public void testComputeFlipScaleXAtMidpoint() {
    assertEquals(0.0, FlipAnimationUtils.computeFlipScaleX(0.5), 0.001);
  }

  @Test
  public void testComputeFlipScaleXAtThreeQuarters() {
    assertEquals(0.5, FlipAnimationUtils.computeFlipScaleX(0.75), 0.001);
  }

  @Test
  public void testComputeFlipScaleXAtEnd() {
    assertEquals(1.0, FlipAnimationUtils.computeFlipScaleX(1.0), 0.001);
  }

  @Test
  public void testFlipScaleXIsSymmetric() {
    // The scale at progress p should equal scale at (1.0 - p)
    for (double p = 0.0; p <= 0.5; p += 0.05) {
      double left = FlipAnimationUtils.computeFlipScaleX(p);
      double right = FlipAnimationUtils.computeFlipScaleX(1.0 - p);
      assertEquals("Scale should be symmetric at " + p, left, right, 0.001);
    }
  }

  @Test
  public void testFlipScaleXNeverNegative() {
    for (double p = 0.0; p <= 1.0; p += 0.01) {
      assertTrue("Scale should never be negative at progress " + p,
              FlipAnimationUtils.computeFlipScaleX(p) >= 0.0);
    }
  }

  @Test
  public void testAIvsAISetsAnimationSpeedFast() {
    IReversiModel model = new ReversiModel(3);
    GameHistory history = new GameHistory();
    MockAnimView blackView = new MockAnimView();
    MockAnimView whiteView = new MockAnimView();

    Controller blackCtrl = new Controller(model,
            new AIPlayer(Player.BLACK, new AsManyPiecesAsPossible()), blackView, history);
    Controller whiteCtrl = new Controller(model,
            new AIPlayer(Player.WHITE, new AsManyPiecesAsPossible()), whiteView, history);

    blackCtrl.setOpponent(whiteCtrl);

    assertTrue("Black view should have fast animation for AI vs AI", blackView.fastAnimation);
    assertTrue("White view should have fast animation for AI vs AI", whiteView.fastAnimation);
  }

  @Test
  public void testHumanVsAIDoesNotSetFastAnimation() {
    IReversiModel model = new ReversiModel(3);
    GameHistory history = new GameHistory();
    MockAnimView blackView = new MockAnimView();
    MockAnimView whiteView = new MockAnimView();

    Controller blackCtrl = new Controller(model,
            new HumanPlayer(model, Player.BLACK), blackView, history);
    Controller whiteCtrl = new Controller(model,
            new AIPlayer(Player.WHITE, new AsManyPiecesAsPossible()), whiteView, history);

    blackCtrl.setOpponent(whiteCtrl);

    assertFalse("Black view should not have fast animation for Human vs AI",
            blackView.fastAnimation);
    assertFalse("White view should not have fast animation for Human vs AI",
            whiteView.fastAnimation);
  }

  @Test
  public void testHumanVsHumanDoesNotSetFastAnimation() {
    IReversiModel model = new ReversiModel(3);
    GameHistory history = new GameHistory();
    MockAnimView blackView = new MockAnimView();
    MockAnimView whiteView = new MockAnimView();

    Controller blackCtrl = new Controller(model,
            new HumanPlayer(model, Player.BLACK), blackView, history);
    Controller whiteCtrl = new Controller(model,
            new HumanPlayer(model, Player.WHITE), whiteView, history);

    blackCtrl.setOpponent(whiteCtrl);

    assertFalse("Should not have fast animation for Human vs Human", blackView.fastAnimation);
    assertFalse("Should not have fast animation for Human vs Human", whiteView.fastAnimation);
  }

  /**
   * Mock view that tracks setAnimationSpeed calls.
   */
  private static class MockAnimView implements IGraphicsView {
    boolean fastAnimation = false;

    @Override
    public void setAnimationSpeed(boolean fast) {
      this.fastAnimation = fast;
    }

    @Override
    public void scheduleDelayed(Runnable action, int delayMs) {
      action.run();
    }

    @Override
    public void runOnUIThread(Runnable action) {
      action.run();
    }

    @Override
    public void makeVisible() { }

    @Override
    public void setViewListener(ViewListener listener) { }

    @Override
    public void refresh() { }

    @Override
    public void outOfTurnMessage() { }

    @Override
    public void invalidMoveMessage() { }

    @Override
    public void gameOver(int blackScore, int whiteScore) { }

    @Override
    public void playerTurn() { }

    @Override
    public void highlightLastMove(int placedRow, int placedCol, List<Coordinate> flipped) { }

    @Override
    public void undoNotAvailableMessage() { }

    @Override
    public void updateHistory(List<MoveRecord> records) { }

    @Override
    public void showSaveSuccess() { }

    @Override
    public void showLoadSuccess() { }

    @Override
    public void showFileError(String message) { }
  }
}
