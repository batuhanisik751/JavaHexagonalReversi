package cs3500.reversi.stats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages reading, writing, and computing statistics from game results stored as JSON.
 * Uses manual JSON formatting — no external JSON libraries.
 */
public final class StatsManager {

  private static final File DEFAULT_DIR = new File(System.getProperty("user.home"), ".reversi");
  private static final File DEFAULT_FILE = new File(DEFAULT_DIR, "stats.json");

  private StatsManager() {
    // no instantiation
  }

  /**
   * Appends a game result to the default stats file. Never throws — failures are silently ignored
   * so that stats recording cannot crash the game.
   * @param result the game result to record.
   */
  public static void appendResult(GameResult result) {
    appendResult(result, DEFAULT_FILE);
  }

  /**
   * Appends a game result to the specified file.
   * @param result the game result to record.
   * @param file the file to write to.
   */
  public static void appendResult(GameResult result, File file) {
    try {
      List<GameResult> existing = loadResults(file);
      existing.add(result);
      saveResults(existing, file);
    } catch (IOException e) {
      // Silently ignore — stats must never crash the game
    }
  }

  /**
   * Loads all game results from the default stats file.
   * @return list of game results, or empty list if file is missing or corrupted.
   */
  public static List<GameResult> loadResults() {
    try {
      return loadResults(DEFAULT_FILE);
    } catch (IOException e) {
      return new ArrayList<>();
    }
  }

  /**
   * Loads all game results from the specified file.
   * @param file the file to read from.
   * @return list of game results.
   * @throws IOException if reading fails.
   */
  public static List<GameResult> loadResults(File file) throws IOException {
    if (!file.exists() || file.length() == 0) {
      return new ArrayList<>();
    }
    StringBuilder sb = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line.trim());
      }
    }
    return parseJsonArray(sb.toString());
  }

  /**
   * Saves all game results to the specified file as a JSON array.
   * @param results the results to save.
   * @param file the file to write to.
   * @throws IOException if writing fails.
   */
  public static void saveResults(List<GameResult> results, File file) throws IOException {
    File parent = file.getParentFile();
    if (parent != null && !parent.exists()) {
      parent.mkdirs();
    }
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      writer.write(toJsonArray(results));
    }
  }

  // --- JSON Formatting ---

  static String toJsonArray(List<GameResult> results) {
    StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    for (int i = 0; i < results.size(); i++) {
      sb.append(toJsonObject(results.get(i)));
      if (i < results.size() - 1) {
        sb.append(",");
      }
      sb.append("\n");
    }
    sb.append("]");
    return sb.toString();
  }

  private static String toJsonObject(GameResult r) {
    StringBuilder sb = new StringBuilder();
    sb.append("  {");
    sb.append("\"date\":\"").append(r.getDate()).append("\",");
    sb.append("\"boardSize\":").append(r.getBoardSize()).append(",");
    sb.append("\"player1Type\":\"").append(r.getPlayer1Type()).append("\",");
    sb.append("\"player2Type\":\"").append(r.getPlayer2Type()).append("\",");
    sb.append("\"winner\":\"").append(r.getWinner()).append("\",");
    sb.append("\"blackScore\":").append(r.getBlackScore()).append(",");
    sb.append("\"whiteScore\":").append(r.getWhiteScore()).append(",");
    sb.append("\"moveCount\":").append(r.getMoveCount());
    sb.append("}");
    return sb.toString();
  }

  // --- JSON Parsing ---

  static List<GameResult> parseJsonArray(String json) {
    List<GameResult> results = new ArrayList<>();
    int i = 0;
    while (i < json.length()) {
      int start = json.indexOf('{', i);
      if (start < 0) {
        break;
      }
      int end = json.indexOf('}', start);
      if (end < 0) {
        break;
      }
      String obj = json.substring(start + 1, end);
      GameResult r = parseJsonObject(obj);
      if (r != null) {
        results.add(r);
      }
      i = end + 1;
    }
    return results;
  }

  private static GameResult parseJsonObject(String obj) {
    try {
      Map<String, String> fields = new LinkedHashMap<>();
      // Split by comma, but only at the top level (no nested objects)
      String[] pairs = obj.split(",");
      for (String pair : pairs) {
        int colon = pair.indexOf(':');
        if (colon < 0) {
          continue;
        }
        String key = pair.substring(0, colon).trim();
        String value = pair.substring(colon + 1).trim();
        // Strip quotes from key
        key = stripQuotes(key);
        fields.put(key, value);
      }

      return new GameResult(
              stripQuotes(fields.get("date")),
              Integer.parseInt(fields.get("boardSize")),
              stripQuotes(fields.get("player1Type")),
              stripQuotes(fields.get("player2Type")),
              stripQuotes(fields.get("winner")),
              Integer.parseInt(fields.get("blackScore")),
              Integer.parseInt(fields.get("whiteScore")),
              Integer.parseInt(fields.get("moveCount"))
      );
    } catch (Exception e) {
      return null; // skip corrupted entries
    }
  }

  private static String stripQuotes(String s) {
    if (s == null) {
      return "";
    }
    s = s.trim();
    if (s.startsWith("\"") && s.endsWith("\"")) {
      return s.substring(1, s.length() - 1);
    }
    return s;
  }

  // --- Stats Computation ---

  /**
   * Computes overall win/loss/draw record for human-vs-AI games.
   * A "win" is when the human player's side won.
   * @param results all game results.
   * @return int[3] = {wins, losses, draws}.
   */
  public static int[] getOverallRecord(List<GameResult> results) {
    int wins = 0;
    int losses = 0;
    int draws = 0;
    for (GameResult r : results) {
      String humanSide = getHumanSide(r);
      if (humanSide == null) {
        continue; // skip AI-vs-AI or human-vs-human
      }
      if ("draw".equals(r.getWinner())) {
        draws++;
      } else if (r.getWinner().equals(humanSide)) {
        wins++;
      } else {
        losses++;
      }
    }
    return new int[]{wins, losses, draws};
  }

  /**
   * Computes win/loss/draw records grouped by AI difficulty.
   * @param results all game results.
   * @return map of difficulty → int[3] {wins, losses, draws}.
   */
  public static Map<String, int[]> getRecordByDifficulty(List<GameResult> results) {
    Map<String, int[]> records = new LinkedHashMap<>();
    for (GameResult r : results) {
      String humanSide = getHumanSide(r);
      if (humanSide == null) {
        continue;
      }
      String aiDifficulty = "human".equals(r.getPlayer1Type())
              ? r.getPlayer2Type() : r.getPlayer1Type();
      int[] rec = records.computeIfAbsent(aiDifficulty, k -> new int[3]);
      if ("draw".equals(r.getWinner())) {
        rec[2]++;
      } else if (r.getWinner().equals(humanSide)) {
        rec[0]++;
      } else {
        rec[1]++;
      }
    }
    return records;
  }

  /**
   * Computes the longest consecutive win streak for the human player.
   * @param results all game results (in chronological order).
   * @return the longest win streak.
   */
  public static int getLongestWinStreak(List<GameResult> results) {
    int longest = 0;
    int current = 0;
    for (GameResult r : results) {
      String humanSide = getHumanSide(r);
      if (humanSide == null) {
        continue;
      }
      if (r.getWinner().equals(humanSide)) {
        current++;
        longest = Math.max(longest, current);
      } else {
        current = 0;
      }
    }
    return longest;
  }

  /**
   * Computes the current (most recent) consecutive win streak for the human player.
   * @param results all game results (in chronological order).
   * @return the current win streak.
   */
  public static int getCurrentWinStreak(List<GameResult> results) {
    int current = 0;
    for (int i = results.size() - 1; i >= 0; i--) {
      GameResult r = results.get(i);
      String humanSide = getHumanSide(r);
      if (humanSide == null) {
        continue;
      }
      if (r.getWinner().equals(humanSide)) {
        current++;
      } else {
        break;
      }
    }
    return current;
  }

  /**
   * Finds the game with the most moves played.
   * @param results all game results.
   * @return the game result with the highest move count, or null if empty.
   */
  public static GameResult getLongestGame(List<GameResult> results) {
    GameResult longest = null;
    for (GameResult r : results) {
      if (longest == null || r.getMoveCount() > longest.getMoveCount()) {
        longest = r;
      }
    }
    return longest;
  }

  /**
   * Returns "black" or "white" if exactly one player is human, otherwise null.
   */
  private static String getHumanSide(GameResult r) {
    boolean p1Human = "human".equals(r.getPlayer1Type());
    boolean p2Human = "human".equals(r.getPlayer2Type());
    if (p1Human && !p2Human) {
      return "black";
    } else if (!p1Human && p2Human) {
      return "white";
    }
    return null; // both human or both AI
  }
}
