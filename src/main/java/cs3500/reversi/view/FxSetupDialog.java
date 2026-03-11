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
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A modal JavaFX setup dialog that lets users configure the game before starting.
 * Supports local play, hosting a network game, or joining a network game.
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
  private static final String[] TIMER_OPTIONS = {"No Limit", "10s", "30s", "60s"};
  private static final String[] GAME_MODE_OPTIONS = {"Local Game", "Host Game (LAN)", "Join Game (LAN)"};

  private final Stage stage;
  private final Spinner<Integer> boardSizeSpinner;
  private final ComboBox<String> player1Combo;
  private final ComboBox<String> player2Combo;
  private final ComboBox<String> themeCombo;
  private final ComboBox<String> gameModeCombo;
  private final TextField portField;
  private final TextField hostField;
  private final Label player2Label;
  private final Label boardSizeLabel;
  private final ComboBox<String> timerCombo;
  private final Label portLabel;
  private final Label hostLabel;
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

    int row = 0;

    // Title
    Label title = new Label("Game Setup");
    title.setStyle("-fx-text-fill: " + FG + "; -fx-font-family: 'SansSerif'; "
            + "-fx-font-weight: bold; -fx-font-size: 20px;");
    title.setAlignment(Pos.CENTER);
    title.setMaxWidth(Double.MAX_VALUE);
    GridPane.setColumnSpan(title, 2);
    GridPane.setHalignment(title, HPos.CENTER);
    content.add(title, 0, row++);

    // Game Mode
    content.add(styledLabel("Game Mode:"), 0, row);
    gameModeCombo = new ComboBox<>(FXCollections.observableArrayList(GAME_MODE_OPTIONS));
    gameModeCombo.setStyle(FIELD_STYLE);
    gameModeCombo.setPrefWidth(180);
    gameModeCombo.getSelectionModel().selectFirst();
    gameModeCombo.setOnAction(e -> updateVisibility());
    content.add(gameModeCombo, 1, row++);

    // Board Size
    boardSizeLabel = styledLabel("Board Size:");
    content.add(boardSizeLabel, 0, row);
    boardSizeSpinner = new Spinner<>(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 8, 4));
    boardSizeSpinner.setStyle(FIELD_STYLE);
    boardSizeSpinner.setPrefWidth(180);
    content.add(boardSizeSpinner, 1, row++);

    // Player 1
    content.add(styledLabel("Player 1 (Black):"), 0, row);
    player1Combo = new ComboBox<>(FXCollections.observableArrayList(PLAYER_OPTIONS));
    player1Combo.setStyle(FIELD_STYLE);
    player1Combo.setPrefWidth(180);
    player1Combo.getSelectionModel().selectFirst();
    content.add(player1Combo, 1, row++);

    // Player 2
    player2Label = styledLabel("Player 2 (White):");
    content.add(player2Label, 0, row);
    player2Combo = new ComboBox<>(FXCollections.observableArrayList(PLAYER_OPTIONS));
    player2Combo.setStyle(FIELD_STYLE);
    player2Combo.setPrefWidth(180);
    player2Combo.getSelectionModel().selectFirst();
    content.add(player2Combo, 1, row++);

    // Port
    portLabel = styledLabel("Port:");
    content.add(portLabel, 0, row);
    portField = new TextField("12345");
    portField.setStyle(FIELD_STYLE);
    portField.setPrefWidth(180);
    content.add(portField, 1, row++);

    // Host address (join mode only)
    hostLabel = styledLabel("Host Address:");
    content.add(hostLabel, 0, row);
    hostField = new TextField("localhost");
    hostField.setStyle(FIELD_STYLE);
    hostField.setPrefWidth(180);
    content.add(hostField, 1, row++);

    // Theme
    content.add(styledLabel("Theme:"), 0, row);
    themeCombo = new ComboBox<>(FXCollections.observableArrayList(THEME_OPTIONS));
    themeCombo.setStyle(FIELD_STYLE);
    themeCombo.setPrefWidth(180);
    themeCombo.getSelectionModel().selectFirst();
    content.add(themeCombo, 1, row++);

    // Move Timer
    content.add(styledLabel("Move Timer:"), 0, row);
    timerCombo = new ComboBox<>(FXCollections.observableArrayList(TIMER_OPTIONS));
    timerCombo.setStyle(FIELD_STYLE);
    timerCombo.setPrefWidth(180);
    timerCombo.getSelectionModel().selectFirst();
    content.add(timerCombo, 1, row++);

    // Start button
    Button startButton = new Button("Start Game");
    startButton.setStyle("-fx-font-family: 'SansSerif'; -fx-font-weight: bold; "
            + "-fx-font-size: 14px;");
    startButton.setOnAction(e -> {
      confirmed = true;
      stage.close();
    });

    Button statsButton = new Button("Statistics");
    statsButton.setStyle("-fx-font-family: 'SansSerif'; -fx-font-weight: bold; "
            + "-fx-font-size: 14px;");
    statsButton.setOnAction(e -> new FxStatsView().showAndWait());

    HBox buttonBox = new HBox(10, statsButton, startButton);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setPadding(new Insets(16, 0, 4, 0));
    GridPane.setColumnSpan(buttonBox, 2);
    content.add(buttonBox, 0, row);

    Scene scene = new Scene(content);
    stage.setScene(scene);

    // Initialize visibility
    updateVisibility();
  }

  private void updateVisibility() {
    String mode = gameModeCombo.getValue();
    boolean isLocal = "Local Game".equals(mode);
    boolean isJoin = "Join Game (LAN)".equals(mode);

    // Board size: visible for local and host (server decides size)
    boardSizeLabel.setVisible(!isJoin);
    boardSizeSpinner.setVisible(!isJoin);
    boardSizeLabel.setManaged(!isJoin);
    boardSizeSpinner.setManaged(!isJoin);

    // Player 2: only visible for local games
    player2Label.setVisible(isLocal);
    player2Combo.setVisible(isLocal);
    player2Label.setManaged(isLocal);
    player2Combo.setManaged(isLocal);

    // Port: visible for host and join
    portLabel.setVisible(!isLocal);
    portField.setVisible(!isLocal);
    portLabel.setManaged(!isLocal);
    portField.setManaged(!isLocal);

    // Host address: only visible for join
    hostLabel.setVisible(isJoin);
    hostField.setVisible(isJoin);
    hostLabel.setManaged(isJoin);
    hostField.setManaged(isJoin);

    // Player 1 combo: hide for join (server assigns color)
    player1Combo.setVisible(!isJoin);
    player1Combo.setManaged(!isJoin);

    if (stage.isShowing()) {
      stage.sizeToScene();
    }
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
   * Returns the game mode: "local", "host", or "join".
   */
  public String getGameMode() {
    String selection = gameModeCombo.getValue();
    if ("Host Game (LAN)".equals(selection)) {
      return "host";
    } else if ("Join Game (LAN)".equals(selection)) {
      return "join";
    }
    return "local";
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

  /**
   * Returns the port number for network play.
   */
  public int getPort() {
    try {
      return Integer.parseInt(portField.getText().trim());
    } catch (NumberFormatException e) {
      return 12345;
    }
  }

  /**
   * Returns the host address for joining a network game.
   */
  public String getHostAddress() {
    return hostField.getText().trim();
  }

  /**
   * Returns the move timer setting in seconds, or 0 for no limit.
   */
  public int getTimerSeconds() {
    String selection = timerCombo.getValue();
    if ("No Limit".equals(selection)) {
      return 0;
    }
    try {
      return Integer.parseInt(selection.replace("s", ""));
    } catch (NumberFormatException e) {
      return 0;
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
