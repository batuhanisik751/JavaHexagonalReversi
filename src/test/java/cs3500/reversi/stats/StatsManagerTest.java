package cs3500.reversi.stats;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for StatsManager JSON I/O and statistics computation.
 */
public class StatsManagerTest {

  private File tempDir;
  private File tempFile;

  @Before
  public void setUp() throws IOException {
    tempDir = Files.createTempDirectory("reversi-stats-test").toFile();
    tempFile = new File(tempDir, "stats.json");
    tempDir.deleteOnExit();
  }

  // --- JSON I/O Tests ---

  @Test
  public void testWriteAndReadRoundTrip() throws IOException {
    GameResult r = new GameResult("2026-03-11", 4, "human", "easy",
            "black", 18, 12, 24);
    List<GameResult> results = new ArrayList<>();
    results.add(r);
    StatsManager.saveResults(results, tempFile);

    List<GameResult> loaded = StatsManager.loadResults(tempFile);
    assertEquals(1, loaded.size());
    GameResult lr = loaded.get(0);
    assertEquals("2026-03-11", lr.getDate());
    assertEquals(4, lr.getBoardSize());
    assertEquals("human", lr.getPlayer1Type());
    assertEquals("easy", lr.getPlayer2Type());
    assertEquals("black", lr.getWinner());
    assertEquals(18, lr.getBlackScore());
    assertEquals(12, lr.getWhiteScore());
    assertEquals(24, lr.getMoveCount());
  }

  @Test
  public void testAppendToExistingFile() throws IOException {
    GameResult r1 = new GameResult("2026-01-01", 3, "human", "easy",
            "black", 10, 5, 15);
    GameResult r2 = new GameResult("2026-02-01", 4, "human", "hard",
            "white", 8, 20, 28);
    StatsManager.appendResult(r1, tempFile);
    StatsManager.appendResult(r2, tempFile);

    List<GameResult> loaded = StatsManager.loadResults(tempFile);
    assertEquals(2, loaded.size());
    assertEquals("2026-01-01", loaded.get(0).getDate());
    assertEquals("2026-02-01", loaded.get(1).getDate());
  }

  @Test
  public void testEmptyOrMissingFile() throws IOException {
    List<GameResult> loaded = StatsManager.loadResults(tempFile);
    assertTrue(loaded.isEmpty());
  }

  @Test
  public void testMultipleResults() throws IOException {
    List<GameResult> results = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      results.add(new GameResult("2026-0" + (i + 1) + "-01", 4, "human", "medium",
              i % 2 == 0 ? "black" : "white", 10 + i, 10 - i, 20 + i));
    }
    StatsManager.saveResults(results, tempFile);
    List<GameResult> loaded = StatsManager.loadResults(tempFile);
    assertEquals(5, loaded.size());
    for (int i = 0; i < 5; i++) {
      assertEquals(results.get(i).getDate(), loaded.get(i).getDate());
      assertEquals(results.get(i).getBlackScore(), loaded.get(i).getBlackScore());
    }
  }

  @Test
  public void testDirectoryCreation() throws IOException {
    File nestedFile = new File(new File(tempDir, "sub"), "stats.json");
    GameResult r = new GameResult("2026-03-11", 4, "human", "easy",
            "black", 15, 10, 25);
    StatsManager.appendResult(r, nestedFile);

    assertTrue(nestedFile.exists());
    List<GameResult> loaded = StatsManager.loadResults(nestedFile);
    assertEquals(1, loaded.size());
  }

  // --- JSON Format Tests ---

  @Test
  public void testJsonRoundTripPreservesAllFields() throws IOException {
    GameResult r = new GameResult("2026-12-31", 7, "hard", "human",
            "draw", 25, 25, 50);
    List<GameResult> results = new ArrayList<>();
    results.add(r);
    StatsManager.saveResults(results, tempFile);

    List<GameResult> loaded = StatsManager.loadResults(tempFile);
    assertEquals(1, loaded.size());
    GameResult lr = loaded.get(0);
    assertEquals("draw", lr.getWinner());
    assertEquals(25, lr.getBlackScore());
    assertEquals(25, lr.getWhiteScore());
    assertEquals(50, lr.getMoveCount());
    assertEquals("hard", lr.getPlayer1Type());
    assertEquals("human", lr.getPlayer2Type());
  }

  // --- Stats Computation Tests ---

  @Test
  public void testOverallRecordHumanVsAI() {
    List<GameResult> results = new ArrayList<>();
    results.add(new GameResult("2026-01-01", 4, "human", "easy", "black", 18, 12, 24));
    results.add(new GameResult("2026-01-02", 4, "human", "easy", "white", 10, 20, 24));
    results.add(new GameResult("2026-01-03", 4, "human", "medium", "draw", 15, 15, 30));

    int[] record = StatsManager.getOverallRecord(results);
    assertEquals(1, record[0]); // wins
    assertEquals(1, record[1]); // losses
    assertEquals(1, record[2]); // draws
  }

  @Test
  public void testOverallRecordSkipsAIvsAI() {
    List<GameResult> results = new ArrayList<>();
    results.add(new GameResult("2026-01-01", 4, "easy", "hard", "black", 18, 12, 24));
    results.add(new GameResult("2026-01-02", 4, "human", "easy", "black", 20, 10, 24));

    int[] record = StatsManager.getOverallRecord(results);
    assertEquals(1, record[0]); // only human game counts
    assertEquals(0, record[1]);
    assertEquals(0, record[2]);
  }

  @Test
  public void testOverallRecordSkipsHumanVsHuman() {
    List<GameResult> results = new ArrayList<>();
    results.add(new GameResult("2026-01-01", 4, "human", "human", "black", 18, 12, 24));

    int[] record = StatsManager.getOverallRecord(results);
    assertEquals(0, record[0]);
    assertEquals(0, record[1]);
    assertEquals(0, record[2]);
  }

  @Test
  public void testOverallRecordHumanAsWhite() {
    List<GameResult> results = new ArrayList<>();
    // Human is player2 (white), and white wins
    results.add(new GameResult("2026-01-01", 4, "easy", "human", "white", 10, 20, 24));

    int[] record = StatsManager.getOverallRecord(results);
    assertEquals(1, record[0]); // human won
    assertEquals(0, record[1]);
    assertEquals(0, record[2]);
  }

  @Test
  public void testRecordByDifficulty() {
    List<GameResult> results = new ArrayList<>();
    results.add(new GameResult("2026-01-01", 4, "human", "easy", "black", 18, 12, 24));
    results.add(new GameResult("2026-01-02", 4, "human", "easy", "white", 10, 20, 24));
    results.add(new GameResult("2026-01-03", 4, "human", "hard", "black", 16, 14, 30));

    Map<String, int[]> byDifficulty = StatsManager.getRecordByDifficulty(results);
    assertEquals(2, byDifficulty.size());

    int[] easy = byDifficulty.get("easy");
    assertNotNull(easy);
    assertEquals(1, easy[0]); // wins vs easy
    assertEquals(1, easy[1]); // losses vs easy

    int[] hard = byDifficulty.get("hard");
    assertNotNull(hard);
    assertEquals(1, hard[0]); // wins vs hard
    assertEquals(0, hard[1]); // losses vs hard
  }

  @Test
  public void testLongestWinStreak() {
    List<GameResult> results = new ArrayList<>();
    results.add(new GameResult("d", 4, "human", "easy", "black", 18, 12, 24)); // win
    results.add(new GameResult("d", 4, "human", "easy", "black", 18, 12, 24)); // win
    results.add(new GameResult("d", 4, "human", "easy", "white", 10, 20, 24)); // loss
    results.add(new GameResult("d", 4, "human", "easy", "black", 18, 12, 24)); // win
    results.add(new GameResult("d", 4, "human", "easy", "black", 18, 12, 24)); // win
    results.add(new GameResult("d", 4, "human", "easy", "black", 18, 12, 24)); // win

    assertEquals(3, StatsManager.getLongestWinStreak(results));
  }

  @Test
  public void testCurrentWinStreak() {
    List<GameResult> results = new ArrayList<>();
    results.add(new GameResult("d", 4, "human", "easy", "black", 18, 12, 24)); // win
    results.add(new GameResult("d", 4, "human", "easy", "white", 10, 20, 24)); // loss
    results.add(new GameResult("d", 4, "human", "easy", "black", 18, 12, 24)); // win
    results.add(new GameResult("d", 4, "human", "easy", "black", 18, 12, 24)); // win

    assertEquals(2, StatsManager.getCurrentWinStreak(results));
  }

  @Test
  public void testCurrentWinStreakBrokenByLoss() {
    List<GameResult> results = new ArrayList<>();
    results.add(new GameResult("d", 4, "human", "easy", "black", 18, 12, 24)); // win
    results.add(new GameResult("d", 4, "human", "easy", "white", 10, 20, 24)); // loss

    assertEquals(0, StatsManager.getCurrentWinStreak(results));
  }

  @Test
  public void testLongestGame() {
    List<GameResult> results = new ArrayList<>();
    results.add(new GameResult("d", 4, "human", "easy", "black", 18, 12, 20));
    results.add(new GameResult("d", 5, "human", "hard", "white", 8, 22, 40));
    results.add(new GameResult("d", 3, "human", "medium", "black", 6, 3, 10));

    GameResult longest = StatsManager.getLongestGame(results);
    assertNotNull(longest);
    assertEquals(40, longest.getMoveCount());
  }

  @Test
  public void testLongestGameEmpty() {
    assertNull(StatsManager.getLongestGame(new ArrayList<>()));
  }

  @Test
  public void testEmptyResultsStats() {
    List<GameResult> empty = new ArrayList<>();
    int[] record = StatsManager.getOverallRecord(empty);
    assertEquals(0, record[0]);
    assertEquals(0, record[1]);
    assertEquals(0, record[2]);
    assertEquals(0, StatsManager.getLongestWinStreak(empty));
    assertEquals(0, StatsManager.getCurrentWinStreak(empty));
    assertTrue(StatsManager.getRecordByDifficulty(empty).isEmpty());
  }
}
