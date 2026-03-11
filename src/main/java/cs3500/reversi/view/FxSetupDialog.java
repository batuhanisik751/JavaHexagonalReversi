package cs3500.reversi.view;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A modal JavaFX setup dialog that lets users configure the game before starting.
 */
public class FxSetupDialog {
  private static final String BG = "#555555";
  private static final String FG = "white";
  private static final String LABEL_STYLE =
          "-fx-text-fill: " + FG + "; -fx-font-family: 'SansSerif'; "
                  + "-fx-font-weight: bold; -fx-font-size: 14px;";
  private static final String FIELD_STYLE =
          "-fx-font-family: 'SansSerif'; -fx-font-size: 14px;";
  private static final String[] PLAYER_OPTIONS =
          {"Human", "AI - Easy", "AI - Medium", "AI - Hard"};
  private static final String[] THEME_OPTIONS = {"Dark", "Classic Green", "High Contrast"};

  private final Stage stage;
  private final Spinner<Integer> boardSizeSpinner;
  private final ComboBox<String> player1Combo;
  private final ComboBox<String> player2Combo;
  private final ComboBox<String> themeCombo;
  private boolean confirmed;

  /**
   * Creates and lays out the setup dialog. Call {@code showAndWait()} to display it.
   */
  public FxSetupDialog() {
    stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setTitle("Reversi \u2014 New Game");
    stage.setResizable(false);

    GridPane content = new GridPane();
    content.setHgap(12);
    content.setVgap(8);
    content.setPadding(new Insets(12));
    content.setStyle("-fx-background-color: " + BG + ";");

    ColumnConstraints labelCol = new ColumnConstraints();
    labelCol.setHalignment(HPos.RIGHT);
    ColumnConstraints fieldCol = new ColumnConstraints();
    fieldCol.setPrefWidth(180);
    content.getColumnConstraints().addAll(labelCol, fieldCol);

    // Title
    Label title = new Label("Game Setup");
    title.setStyle("-fx-text-fill: " + FG + "; -fx-font-family: 'SansSerif'; "
            + "-fx-font-weight: bold; -fx-font-size: 20px;");
    title.setAlignment(Pos.CENTER);
    title.setMaxWidth(Double.MAX_VALUE);
    GridPane.setColumnSpan(title, 2);
    GridPane.setHalignment(title, HPos.CENTER);
    content.add(title, 0, 0);

    // Board Size
    content.add(styledLabel("Board Size:"), 0, 1);
    boardSizeSpinner = new Spinner<>(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 8, 4));
    boardSizeSpinner.setStyle(FIELD_STYLE);
    boardSizeSpinner.setPrefWidth(180);
    content.add(boardSizeSpinner, 1, 1);

    // Player 1
    content.add(styledLabel("Player 1 (Black):"), 0, 2);
    player1Combo = new ComboBox<>(FXCollections.observableArrayList(PLAYER_OPTIONS));
    player1Combo.setStyle(FIELD_STYLE);
    player1Combo.setPrefWidth(180);
    player1Combo.getSelectionModel().selectFirst();
    content.add(player1Combo, 1, 2);

    // Player 2
    content.add(styledLabel("Player 2 (White):"), 0, 3);
    player2Combo = new ComboBox<>(FXCollections.observableArrayList(PLAYER_OPTIONS));
    player2Combo.setStyle(FIELD_STYLE);
    player2Combo.setPrefWidth(180);
    player2Combo.getSelectionModel().selectFirst();
    content.add(player2Combo, 1, 3);

    // Theme
    content.add(styledLabel("Theme:"), 0, 4);
    themeCombo = new ComboBox<>(FXCollections.observableArrayList(THEME_OPTIONS));
    themeCombo.setStyle(FIELD_STYLE);
    themeCombo.setPrefWidth(180);
    themeCombo.getSelectionModel().selectFirst();
    content.add(themeCombo, 1, 4);

    // Start button
    Button startButton = new Button("Start Game");
    startButton.setStyle("-fx-font-family: 'SansSerif'; -fx-font-weight: bold; "
            + "-fx-font-size: 14px;");
    startButton.setOnAction(e -> {
      confirmed = true;
      stage.close();
    });

    HBox buttonBox = new HBox(startButton);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setPadding(new Insets(16, 0, 4, 0));
    GridPane.setColumnSpan(buttonBox, 2);
    content.add(buttonBox, 0, 5);

    Scene scene = new Scene(content);
    stage.setScene(scene);
  }

  /**
   * Shows the dialog and blocks until it is closed.
   */
  public void showAndWait() {
    stage.showAndWait();
  }

  private Label styledLabel(String text) {
    Label label = new Label(text);
    label.setStyle(LABEL_STYLE);
    return label;
  }

  /**
   * Returns whether the user clicked "Start Game" (true) or closed the dialog (false).
   */
  public boolean isConfirmed() {
    return confirmed;
  }

  /**
   * Returns the selected board size.
   */
  public int getBoardSize() {
    return boardSizeSpinner.getValue();
  }

  /**
   * Returns the player 1 type: "human", "easy", "medium", or "hard".
   */
  public String getPlayer1Type() {
    return parsePlayerType(player1Combo.getValue());
  }

  /**
   * Returns the player 2 type: "human", "easy", "medium", or "hard".
   */
  public String getPlayer2Type() {
    return parsePlayerType(player2Combo.getValue());
  }

  /**
   * Returns the selected theme name: "dark", "classic", or "highcontrast".
   */
  public String getThemeName() {
    String selection = themeCombo.getValue();
    switch (selection) {
      case "Classic Green":
        return "classic";
      case "High Contrast":
        return "highcontrast";
      default:
        return "dark";
    }
  }

  private String parsePlayerType(String selection) {
    switch (selection) {
      case "AI - Easy":
        return "easy";
      case "AI - Medium":
        return "medium";
      case "AI - Hard":
        return "hard";
      default:
        return "human";
    }
  }
}
