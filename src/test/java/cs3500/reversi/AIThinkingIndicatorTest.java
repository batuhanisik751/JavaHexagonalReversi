package cs3500.reversi;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
import cs3500.reversi.strategy.IReversiStrategies;
import cs3500.reversi.view.IGraphicsView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the AI thinking indicator feature.
 * Verifies that showThinking(true) is called before AI computation
 * and showThinking(false) is called after, including edge cases.
 */
public class AIThinkingIndicatorTest {

  private IReversiModel model;
  private GameHistory history;
  private MockView blackView;
  private MockView whiteView;

  @Before
  public void setUp() {
    model = new ReversiModel(3);
    history = new GameHistory();
    blackView = new MockView();
    whiteView = new MockView();
  }

  @Test
  public void testAIShowsThinkingOnStart() {
    // Black is AI, it's Black's turn first
    Controller blackCtrl = new Controller(model, new AIPlayer(Player.BLACK,
            new AsManyPiecesAsPossible()), blackView, history);
    Controller whiteCtrl = new Controller(model, new HumanPlayer(model, Player.WHITE),
            whiteView, history);
    blackCtrl.setOpponent(whiteCtrl);
    whiteCtrl.setOpponent(blackCtrl);

    blackCtrl.start();

    // showThinking(true) called, then the delayed action calls showThinking(false)
    // Since MockView executes scheduleDelayed immediately, both should have been called
    assertTrue("showThinking history should contain true then false",
            blackView.thinkingHistory.size() >= 2);
    assertTrue("First call should be showThinking(true)", blackView.thinkingHistory.get(0));
    assertFalse("Second call should be showThinking(false)", blackView.thinkingHistory.get(1));
  }

  @Test
  public void testHumanPlayerDoesNotShowThinking() {
    Controller blackCtrl = new Controller(model, new HumanPlayer(model, Player.BLACK),
            blackView, history);
    Controller whiteCtrl = new Controller(model, new HumanPlayer(model, Player.WHITE),
            whiteView, history);
    blackCtrl.setOpponent(whiteCtrl);
    whiteCtrl.setOpponent(blackCtrl);

    blackCtrl.start();

    assertTrue("Human player should never trigger showThinking",
            blackView.thinkingHistory.isEmpty());
  }

  @Test
  public void testThinkingHiddenAfterStrategyException() {
    // Strategy that always throws
    IReversiStrategies failingStrategy = (m, p) -> {
      throw new RuntimeException("Strategy failed");
    };

    Controller blackCtrl = new Controller(model, new AIPlayer(Player.BLACK, failingStrategy),
            blackView, history);
    Controller whiteCtrl = new Controller(model, new HumanPlayer(model, Player.WHITE),
            whiteView, history);
    blackCtrl.setOpponent(whiteCtrl);
    whiteCtrl.setOpponent(blackCtrl);

    blackCtrl.start();

    // Even with exception, showThinking(false) must be called (in finally block)
    assertTrue("Should have called showThinking(true)", blackView.thinkingHistory.contains(true));
    assertEquals("Last showThinking call should be false",
            Boolean.FALSE, blackView.thinkingHistory.get(blackView.thinkingHistory.size() - 1));
  }

  @Test
  public void testThinkingOrderIsCorrect() {
    Controller blackCtrl = new Controller(model, new AIPlayer(Player.BLACK,
            new AsManyPiecesAsPossible()), blackView, history);
    Controller whiteCtrl = new Controller(model, new HumanPlayer(model, Player.WHITE),
            whiteView, history);
    blackCtrl.setOpponent(whiteCtrl);
    whiteCtrl.setOpponent(blackCtrl);

    blackCtrl.start();

    // Verify the pattern: true must come before false
    int firstTrue = blackView.thinkingHistory.indexOf(true);
    int firstFalse = blackView.thinkingHistory.indexOf(false);
    assertTrue("showThinking(true) should be called", firstTrue >= 0);
    assertTrue("showThinking(false) should be called", firstFalse >= 0);
    assertTrue("showThinking(true) should come before showThinking(false)",
            firstTrue < firstFalse);
  }

  /**
   * Mock IGraphicsView that records showThinking calls and executes delayed actions immediately.
   */
  private static class MockView implements IGraphicsView {
    final List<Boolean> thinkingHistory = new ArrayList<>();

    @Override
    public void showThinking(boolean thinking) {
      thinkingHistory.add(thinking);
    }

    @Override
    public void scheduleDelayed(Runnable action, int delayMs) {
      // Execute immediately for testing
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
