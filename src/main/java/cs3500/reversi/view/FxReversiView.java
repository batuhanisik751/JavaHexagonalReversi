package cs3500.reversi.view;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import cs3500.reversi.audio.SoundManager;
import cs3500.reversi.controller.ViewListener;
import cs3500.reversi.history.MoveRecord;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.Player;

/**
 * JavaFX implementation of the graphical view for the Reversi game.
 */
public class FxReversiView implements IGraphicsView {
  private final IReadOnlyReversiModel model;
  private final FxReversiCanvas reversiCanvas;
  private final Label blackScoreLabel;
  private final Label whiteScoreLabel;
  private final Label turnLabel;
  private final Player player;
  private final FxTheme theme;
  private final FxHistoryPanel historyPanel;
  private final Stage stage;
  private Timeline thinkingTimeline;
  private Runnable restartAction;
  private ViewListener viewListener;

  /**
   * Constructs a new FxReversiView with the given model, player, and theme.
   * @param model the Reversi model to display.
   * @param player the player this view belongs to.
   * @param theme the JavaFX color theme.
   * @param stage the JavaFX stage (window) to use.
   */
  public FxReversiView(IReadOnlyReversiModel model, Player player, FxTheme theme, Stage stage) {
    this.model = model;
    this.player = player;
    this.theme = theme;
    this.stage = stage;

    stage.setTitle("Reversi - " + (player == Player.BLACK ? "Black (X)" : "White (O)"));
    int windowSizeX = (int) ((model.getBoardSize() * 2) * FxReversiCanvas.HEX_WIDTH);
    int windowSizeY = (int) ((model.getBoardSize() * 2) * FxReversiCanvas.HEX_HEIGHT);

    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: " + toHex(theme.boardBackground()) + ";");

    // Board canvas (center)
    reversiCanvas = new FxReversiCanvas(model, theme);
    root.setCenter(reversiCanvas);

    // Score panel (top)
    blackScoreLabel = new Label();
    styleLabel(blackScoreLabel, theme.scoreLabelFg());

    whiteScoreLabel = new Label();
    styleLabel(whiteScoreLabel, theme.scoreLabelFg());

    turnLabel = new Label();
    styleLabel(turnLabel, theme.turnLabelActive());

    HBox scorePanel = new HBox(40, blackScoreLabel, whiteScoreLabel, turnLabel);
    scorePanel.setAlignment(Pos.CENTER);
    scorePanel.setPadding(new Insets(5));
    scorePanel.setStyle("-fx-background-color: " + toHex(theme.scorePanelBg()) + ";");
    root.setTop(scorePanel);

    // Button panel (bottom)
    Button saveButton = new Button("Save");
    saveButton.setOnAction(e -> { if (viewListener != null) viewListener.onSave(); });

    Button loadButton = new Button("Load");
    loadButton.setOnAction(e -> { if (viewListener != null) viewListener.onLoad(); });

    Button undoButton = new Button("Undo");
    undoButton.setOnAction(e -> { if (viewListener != null) viewListener.onUndo(); });

    Button quitButton = new Button("Quit");
    quitButton.setOnAction(e -> Platform.exit());

    ToggleButton muteButton = new ToggleButton("Mute");
    muteButton.setSelected(SoundManager.isMuted());
    muteButton.setOnAction(e -> SoundManager.setMuted(muteButton.isSelected()));

    HBox buttonPanel = new HBox(10, saveButton, loadButton, undoButton, quitButton, muteButton);
    buttonPanel.setAlignment(Pos.CENTER);
    buttonPanel.setPadding(new Insets(5));
    root.setBottom(buttonPanel);

    // History panel (right)
    historyPanel = new FxHistoryPanel();
    root.setRight(historyPanel);

    Scene scene = new Scene(root, windowSizeX, windowSizeY);
    stage.setScene(scene);

    updateStatusLabels();

    // Ensure canvas gets focus for keyboard input after scene is shown
    Platform.runLater(() -> reversiCanvas.requestFocus());
  }

  @Override
  public void setViewListener(ViewListener listener) {
    this.viewListener = listener;
    this.reversiCanvas.setViewListener(listener);
  }

  /**
   * Sets the action to run when the user chooses "Play Again" at game over.
   * @param restartAction the action that restarts the game.
   */
  public void setRestartAction(Runnable restartAction) {
    this.restartAction = restartAction;
  }

  @Override
  public void makeVisible() {
    stage.show();
    reversiCanvas.draw();
  }

  @Override
  public void refresh() {
    updateStatusLabels();
    reversiCanvas.draw();
  }

  @Override
  public void outOfTurnMessage() {
    showAlert(Alert.AlertType.INFORMATION, "Out of Turn",
            "It's not your turn. Please wait for your turn.");
  }

  @Override
  public void invalidMoveMessage() {
    showAlert(Alert.AlertType.ERROR, "Invalid Move",
            "Invalid move. Please try again.");
  }

  @Override
  public void gameOver(int blackScore, int whiteScore) {
    String winner;
    if (blackScore > whiteScore) {
      winner = "Black (X) wins!";
    } else if (whiteScore > blackScore) {
      winner = "White (O) wins!";
    } else {
      winner = "It's a tie!";
    }
    String message = winner + "\n\nBlack Score: " + blackScore + "\nWhite Score: " + whiteScore;

    ButtonType playAgain = new ButtonType("Play Again");
    ButtonType quit = new ButtonType("Quit");
    Alert alert = new Alert(Alert.AlertType.INFORMATION, message, playAgain, quit);
    alert.setTitle("Game Over");
    alert.setHeaderText(null);
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == playAgain && restartAction != null) {
      restartAction.run();
    } else {
      Platform.exit();
    }
  }

  @Override
  public void playerTurn() {
    String message = "Player " + model.getCurrentTurn() + "'s turn.";
    showAlert(Alert.AlertType.INFORMATION, "Player Turn", message);
  }

  @Override
  public void highlightLastMove(int placedRow, int placedCol, List<Coordinate> flipped) {
    reversiCanvas.setHighlights(placedRow, placedCol, flipped);
  }

  @Override
  public void undoNotAvailableMessage() {
    showAlert(Alert.AlertType.INFORMATION, "Undo Not Available", "No move to undo.");
  }

  @Override
  public void updateHistory(List<MoveRecord> records) {
    historyPanel.updateHistory(records);
  }

  @Override
  public void showSaveSuccess() {
    showAlert(Alert.AlertType.INFORMATION, "Save", "Game saved successfully.");
  }

  @Override
  public void showLoadSuccess() {
    showAlert(Alert.AlertType.INFORMATION, "Load", "Game loaded successfully.");
  }

  @Override
  public void showFileError(String message) {
    showAlert(Alert.AlertType.ERROR, "File Error", message);
  }

  @Override
  public void scheduleDelayed(Runnable action, int delayMs) {
    PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
    pause.setOnFinished(e -> action.run());
    pause.play();
  }

  @Override
  public void runOnUIThread(Runnable action) {
    Platform.runLater(action);
  }

  @Override
  public File showSaveFileChooser() {
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Reversi Save (.reversi)", "*.reversi"));
    File file = chooser.showSaveDialog(stage);
    if (file != null && !file.getName().endsWith(".reversi")) {
      file = new File(file.getAbsolutePath() + ".reversi");
    }
    return file;
  }

  @Override
  public File showLoadFileChooser() {
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Reversi Save (.reversi)", "*.reversi"));
    return chooser.showOpenDialog(stage);
  }

  @Override
  public void showThinking(boolean thinking) {
    if (thinking) {
      turnLabel.setText("Thinking...");
      turnLabel.setStyle(labelStyle(theme.turnLabelActive()));
      String[] frames = {"Thinking.", "Thinking..", "Thinking..."};
      thinkingTimeline = new Timeline(new KeyFrame(Duration.millis(400), e -> {
        String current = turnLabel.getText();
        if (frames[0].equals(current)) {
          turnLabel.setText(frames[1]);
        } else if (frames[1].equals(current)) {
          turnLabel.setText(frames[2]);
        } else {
          turnLabel.setText(frames[0]);
        }
      }));
      thinkingTimeline.setCycleCount(Animation.INDEFINITE);
      thinkingTimeline.play();
    } else {
      if (thinkingTimeline != null) {
        thinkingTimeline.stop();
        thinkingTimeline = null;
      }
      updateStatusLabels();
    }
  }

  @Override
  public String showDisconnectDialog() {
    ButtonType saveButton = new ButtonType("Save Game");
    ButtonType returnButton = new ButtonType("Return to Setup");
    Alert alert = new Alert(Alert.AlertType.WARNING,
            "Your opponent has disconnected.\n\nWould you like to save the game or return to setup?",
            saveButton, returnButton);
    alert.setTitle("Opponent Disconnected");
    alert.setHeaderText(null);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == saveButton) {
      return "save";
    }
    return "return";
  }

  @Override
  public void setStatusMessage(String message) {
    if (message != null) {
      turnLabel.setText(message);
      turnLabel.setStyle(labelStyle(theme.turnLabelInactive()));
    }
  }

  /**
   * Returns the stage owned by this view.
   */
  public Stage getStage() {
    return stage;
  }

  private void updateStatusLabels() {
    blackScoreLabel.setText("Black (X): " + model.getScore(Player.BLACK));
    whiteScoreLabel.setText("White (O): " + model.getScore(Player.WHITE));
    if (model.gameOver()) {
      turnLabel.setText("Game Over");
      turnLabel.setStyle(labelStyle(theme.turnLabelInactive()));
    } else if (model.getCurrentTurn() == player) {
      turnLabel.setText("Your Turn");
      turnLabel.setStyle(labelStyle(theme.turnLabelActive()));
    } else {
      turnLabel.setText("Waiting...");
      turnLabel.setStyle(labelStyle(theme.turnLabelInactive()));
    }
  }

  private void showAlert(Alert.AlertType type, String title, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  private void styleLabel(Label label, javafx.scene.paint.Color color) {
    label.setStyle(labelStyle(color));
  }

  private String labelStyle(javafx.scene.paint.Color color) {
    return "-fx-text-fill: " + toHex(color) + "; "
            + "-fx-font-family: 'SansSerif'; -fx-font-weight: bold; -fx-font-size: 16px;";
  }

  private static String toHex(javafx.scene.paint.Color c) {
    return String.format("#%02X%02X%02X",
            (int) (c.getRed() * 255),
            (int) (c.getGreen() * 255),
            (int) (c.getBlue() * 255));
  }
}
