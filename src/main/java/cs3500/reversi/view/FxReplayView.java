package cs3500.reversi.view;

import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import cs3500.reversi.controller.ReplayController;
import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.Player;

/**
 * A JavaFX window for replaying a completed game move-by-move.
 * Reuses FxReversiCanvas for board rendering.
 */
class FxReplayView {
  private final ReplayController replay;
  private final FxReversiCanvas canvas;
  private final Stage stage;
  private final Label blackScoreLabel;
  private final Label whiteScoreLabel;
  private final Label stepLabel;
  private final Label moveInfoLabel;
  private final Button playPauseButton;
  private final FxHistoryPanel historyPanel;
  private Timeline autoPlay;

  /**
   * Constructs a replay view for the given game history.
   * @param boardSize the board size of the game.
   * @param moves the list of move records to replay.
   * @param theme the color theme to use.
   */
  FxReplayView(int boardSize, List<MoveRecord> moves, FxTheme theme) {
    this.replay = new ReplayController(boardSize, moves);
    this.stage = new Stage();
    stage.setTitle("Game Replay");

    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: " + toHex(theme.boardBackground()) + ";");

    // Board canvas (center)
    canvas = new FxReversiCanvas(replay.getModel(), theme);
    root.setCenter(canvas);

    // Score panel (top)
    blackScoreLabel = new Label();
    styleLabel(blackScoreLabel, theme.scoreLabelFg());

    whiteScoreLabel = new Label();
    styleLabel(whiteScoreLabel, theme.scoreLabelFg());

    stepLabel = new Label();
    styleLabel(stepLabel, theme.turnLabelActive());

    moveInfoLabel = new Label();
    styleLabel(moveInfoLabel, theme.turnLabelInactive());

    HBox scorePanel = new HBox(30, blackScoreLabel, whiteScoreLabel, stepLabel, moveInfoLabel);
    scorePanel.setAlignment(Pos.CENTER);
    scorePanel.setPadding(new Insets(5));
    scorePanel.setStyle("-fx-background-color: " + toHex(theme.scorePanelBg()) + ";");
    root.setTop(scorePanel);

    // Replay controls (bottom)
    Button startButton = new Button("|<");
    startButton.setOnAction(e -> { goToStart(); });

    Button backButton = new Button("<");
    backButton.setOnAction(e -> { stepBack(); });

    playPauseButton = new Button("Play");
    playPauseButton.setOnAction(e -> { togglePlayPause(); });

    Button forwardButton = new Button(">");
    forwardButton.setOnAction(e -> { stepForward(); });

    Button endButton = new Button(">|");
    endButton.setOnAction(e -> { goToEnd(); });

    Button closeButton = new Button("Close");
    closeButton.setOnAction(e -> {
      stopAutoPlay();
      stage.close();
    });

    HBox controlPanel = new HBox(10, startButton, backButton, playPauseButton,
            forwardButton, endButton, closeButton);
    controlPanel.setAlignment(Pos.CENTER);
    controlPanel.setPadding(new Insets(5));
    root.setBottom(controlPanel);

    // History panel (right)
    historyPanel = new FxHistoryPanel();
    root.setRight(historyPanel);

    int windowSizeX = (int) ((boardSize * 2) * FxReversiCanvas.HEX_WIDTH);
    int windowSizeY = (int) ((boardSize * 2) * FxReversiCanvas.HEX_HEIGHT);
    Scene scene = new Scene(root, windowSizeX, windowSizeY);
    stage.setScene(scene);

    updateDisplay();
  }

  /**
   * Shows the replay window.
   */
  void show() {
    stage.show();
    canvas.draw();
  }

  private void stepForward() {
    stopAutoPlay();
    if (replay.stepForward()) {
      updateDisplay();
    }
  }

  private void stepBack() {
    stopAutoPlay();
    if (replay.stepBack()) {
      updateDisplay();
    }
  }

  private void goToStart() {
    stopAutoPlay();
    replay.goToStart();
    updateDisplay();
  }

  private void goToEnd() {
    stopAutoPlay();
    replay.goToEnd();
    updateDisplay();
  }

  private void togglePlayPause() {
    if (autoPlay != null && autoPlay.getStatus() == Animation.Status.RUNNING) {
      stopAutoPlay();
    } else {
      startAutoPlay();
    }
  }

  private void startAutoPlay() {
    if (replay.getCurrentStep() >= replay.getTotalSteps()) {
      // If at the end, restart from beginning
      replay.goToStart();
      updateDisplay();
    }
    autoPlay = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
      if (!replay.stepForward()) {
        stopAutoPlay();
        return;
      }
      updateDisplay();
    }));
    autoPlay.setCycleCount(Animation.INDEFINITE);
    autoPlay.play();
    playPauseButton.setText("Pause");
  }

  private void stopAutoPlay() {
    if (autoPlay != null) {
      autoPlay.stop();
      autoPlay = null;
    }
    playPauseButton.setText("Play");
  }

  private void updateDisplay() {
    IReadOnlyReversiModel model = replay.getModel();

    // Update scores
    blackScoreLabel.setText("Black (X): " + model.getScore(Player.BLACK));
    whiteScoreLabel.setText("White (O): " + model.getScore(Player.WHITE));

    // Update step counter
    stepLabel.setText("Move " + replay.getCurrentStep() + " / " + replay.getTotalSteps());

    // Update move info
    MoveRecord current = replay.getCurrentRecord();
    if (current != null) {
      moveInfoLabel.setText(current.toDisplayString());
    } else {
      moveInfoLabel.setText("Start");
    }

    // Update highlights
    if (current != null && !current.isPass()) {
      canvas.setHighlights(current.getRow(), current.getCol(), replay.getCurrentFlipped());
    } else {
      canvas.clearHighlights();
    }

    // Update history panel
    historyPanel.updateHistory(replay.getHistoryUpToCurrent());

    canvas.draw();
  }

  private void styleLabel(Label label, javafx.scene.paint.Color color) {
    label.setStyle("-fx-text-fill: " + toHex(color) + "; "
            + "-fx-font-family: 'SansSerif'; -fx-font-weight: bold; -fx-font-size: 16px;");
  }

  private static String toHex(javafx.scene.paint.Color c) {
    return String.format("#%02X%02X%02X",
            (int) (c.getRed() * 255),
            (int) (c.getGreen() * 255),
            (int) (c.getBlue() * 255));
  }
}
