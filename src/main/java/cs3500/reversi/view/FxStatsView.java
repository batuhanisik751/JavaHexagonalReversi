package cs3500.reversi.view;

import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import cs3500.reversi.stats.GameResult;
import cs3500.reversi.stats.StatsManager;

/**
 * JavaFX window that displays win/loss statistics loaded from the stats file.
 */
public class FxStatsView {

  private static final String BG = "#555555";
  private static final String FG = "white";
  private static final String TITLE_STYLE =
          "-fx-text-fill: " + FG + "; -fx-font-family: 'SansSerif'; "
                  + "-fx-font-weight: bold; -fx-font-size: 20px;";
  private static final String HEADER_STYLE =
          "-fx-text-fill: " + FG + "; -fx-font-family: 'SansSerif'; "
                  + "-fx-font-weight: bold; -fx-font-size: 16px;";
  private static final String LABEL_STYLE =
          "-fx-text-fill: " + FG + "; -fx-font-family: 'SansSerif'; -fx-font-size: 14px;";

  private final Stage stage;

  /**
   * Constructs a new FxStatsView that loads and displays statistics.
   */
  public FxStatsView() {
    this.stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setTitle("Game Statistics");

    List<GameResult> results = StatsManager.loadResults();

    VBox root = new VBox(8);
    root.setPadding(new Insets(16));
    root.setAlignment(Pos.TOP_LEFT);
    root.setStyle("-fx-background-color: " + BG + ";");

    // Title
    Label title = new Label("Game Statistics");
    title.setStyle(TITLE_STYLE);
    root.getChildren().add(title);

    if (results.isEmpty()) {
      Label noData = new Label("No games played yet.");
      noData.setStyle(LABEL_STYLE);
      root.getChildren().add(noData);
    } else {
      buildStatsContent(root, results);
    }

    // Close button
    root.getChildren().add(new Separator());
    Button closeButton = new Button("Close");
    closeButton.setOnAction(e -> stage.close());
    VBox buttonBox = new VBox(closeButton);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setPadding(new Insets(8, 0, 0, 0));
    root.getChildren().add(buttonBox);

    Scene scene = new Scene(root, 360, 420);
    stage.setScene(scene);
  }

  /**
   * Shows the stats window and blocks until it is closed.
   */
  public void showAndWait() {
    stage.showAndWait();
  }

  private void buildStatsContent(VBox root, List<GameResult> results) {
    // Overall record
    root.getChildren().add(new Separator());
    root.getChildren().add(header("Overall Record (Human vs AI)"));
    int[] overall = StatsManager.getOverallRecord(results);
    root.getChildren().add(label("Wins: " + overall[0]
            + "  |  Losses: " + overall[1] + "  |  Draws: " + overall[2]));

    int totalHumanGames = overall[0] + overall[1] + overall[2];
    if (totalHumanGames > 0) {
      int winPct = (int) Math.round(100.0 * overall[0] / totalHumanGames);
      root.getChildren().add(label("Win rate: " + winPct + "%"));
    }

    // Record by difficulty
    Map<String, int[]> byDifficulty = StatsManager.getRecordByDifficulty(results);
    if (!byDifficulty.isEmpty()) {
      root.getChildren().add(new Separator());
      root.getChildren().add(header("Record by AI Difficulty"));
      for (Map.Entry<String, int[]> entry : byDifficulty.entrySet()) {
        int[] rec = entry.getValue();
        String diffLabel = capitalize(entry.getKey());
        root.getChildren().add(label(diffLabel + ":  "
                + rec[0] + "W / " + rec[1] + "L / " + rec[2] + "D"));
      }
    }

    // Streaks
    root.getChildren().add(new Separator());
    root.getChildren().add(header("Streaks"));
    root.getChildren().add(label("Current win streak: "
            + StatsManager.getCurrentWinStreak(results)));
    root.getChildren().add(label("Longest win streak: "
            + StatsManager.getLongestWinStreak(results)));

    // Records
    root.getChildren().add(new Separator());
    root.getChildren().add(header("Records"));
    root.getChildren().add(label("Total games played: " + results.size()));
    GameResult longest = StatsManager.getLongestGame(results);
    if (longest != null) {
      root.getChildren().add(label("Longest game: " + longest.getMoveCount() + " moves"
              + " (board " + longest.getBoardSize() + ", "
              + longest.getBlackScore() + "-" + longest.getWhiteScore() + ")"));
    }
  }

  private Label header(String text) {
    Label l = new Label(text);
    l.setStyle(HEADER_STYLE);
    return l;
  }

  private Label label(String text) {
    Label l = new Label(text);
    l.setStyle(LABEL_STYLE);
    return l;
  }

  private static String capitalize(String s) {
    if (s == null || s.isEmpty()) {
      return s;
    }
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }
}
