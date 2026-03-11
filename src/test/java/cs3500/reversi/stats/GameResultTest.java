package cs3500.reversi.stats;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the GameResult data model.
 */
public class GameResultTest {

  @Test
  public void testConstructorAndGetters() {
    GameResult r = new GameResult("2026-03-11", 4, "human", "easy",
            "black", 18, 12, 24);
    assertEquals("2026-03-11", r.getDate());
    assertEquals(4, r.getBoardSize());
    assertEquals("human", r.getPlayer1Type());
    assertEquals("easy", r.getPlayer2Type());
    assertEquals("black", r.getWinner());
    assertEquals(18, r.getBlackScore());
    assertEquals(12, r.getWhiteScore());
    assertEquals(24, r.getMoveCount());
  }

  @Test
  public void testDrawResult() {
    GameResult r = new GameResult("2026-01-01", 3, "human", "hard",
            "draw", 10, 10, 20);
    assertEquals("draw", r.getWinner());
    assertEquals(10, r.getBlackScore());
    assertEquals(10, r.getWhiteScore());
  }

  @Test
  public void testWhiteWins() {
    GameResult r = new GameResult("2026-06-15", 5, "medium", "human",
            "white", 8, 22, 30);
    assertEquals("white", r.getWinner());
    assertEquals("medium", r.getPlayer1Type());
    assertEquals("human", r.getPlayer2Type());
  }
}
