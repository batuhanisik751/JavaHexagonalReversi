package cs3500.reversi;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import cs3500.reversi.controller.AIPlayer;
import cs3500.reversi.controller.Controller;
import cs3500.reversi.controller.HumanPlayer;
import cs3500.reversi.controller.PlayerType;
import cs3500.reversi.history.GameHistory;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.strategy.AlphaBetaMiniMax;
import cs3500.reversi.strategy.AsManyPiecesAsPossible;
import cs3500.reversi.strategy.AvoidNextToCorners;
import cs3500.reversi.strategy.CornersFirst;
import cs3500.reversi.strategy.IReversiStrategies;
import cs3500.reversi.strategy.MiniMax;
import cs3500.reversi.view.FxClassicTheme;
import cs3500.reversi.view.FxDarkTheme;
import cs3500.reversi.view.FxHighContrastTheme;
import cs3500.reversi.view.FxReversiView;
import cs3500.reversi.view.FxSetupDialog;
import cs3500.reversi.view.FxTheme;

/**
 * JavaFX application entry point for the Reversi game.
 */
public class ReversiApp extends Application {

  @Override
  public void start(Stage primaryStage) {
    Platform.setImplicitExit(false);
    showSetupAndStart(primaryStage);
  }

  private void showSetupAndStart(Stage primaryStage) {
    FxSetupDialog dialog = new FxSetupDialog();
    dialog.showAndWait();
    if (!dialog.isConfirmed()) {
      Platform.exit();
      return;
    }
    startGameFromConfig(primaryStage, dialog.getBoardSize(), dialog.getPlayer1Type(),
            dialog.getPlayer2Type(), resolveTheme(dialog.getThemeName()));
  }

  private void startGameFromConfig(Stage primaryStage, int boardSize,
                                    String p1Type, String p2Type, FxTheme theme) {
    IReversiModel model = new ReversiModel(boardSize);
    PlayerType player1 = createPlayer(model, Player.BLACK, p1Type);
    PlayerType player2 = createPlayer(model, Player.WHITE, p2Type);

    FxReversiView viewPlayer1 = new FxReversiView(model, Player.BLACK, theme, primaryStage);
    Stage stage2 = new Stage();
    FxReversiView viewPlayer2 = new FxReversiView(model, Player.WHITE, theme, stage2);

    Runnable restart = () -> {
      primaryStage.close();
      stage2.close();
      showSetupAndStart(new Stage());
    };
    viewPlayer1.setRestartAction(restart);
    viewPlayer2.setRestartAction(restart);

    GameHistory history = new GameHistory();
    Controller controller1 = new Controller(model, player1, viewPlayer1, history);
    Controller controller2 = new Controller(model, player2, viewPlayer2, history);
    controller1.setOpponent(controller2);
    controller2.setOpponent(controller1);
    controller1.start();
    controller2.start();
  }

  private PlayerType createPlayer(IReversiModel model, Player color, String type) {
    if (type.equals("human")) {
      return new HumanPlayer(model, color);
    } else {
      return new AIPlayer(color, findStrategy(type));
    }
  }

  private FxTheme resolveTheme(String name) {
    switch (name) {
      case "classic":
        return new FxClassicTheme();
      case "highcontrast":
        return new FxHighContrastTheme();
      default:
        return new FxDarkTheme();
    }
  }

  private IReversiStrategies findStrategy(String strategy) {
    switch (strategy) {
      case "easy":
      case "strategy1":
        return new AsManyPiecesAsPossible();
      case "medium":
      case "strategy3":
        return new CornersFirst();
      case "hard":
        return new AlphaBetaMiniMax(3);
      case "strategy2":
        return new AvoidNextToCorners();
      case "strategy4":
        return new MiniMax();
      default:
        throw new IllegalArgumentException("Input a valid strategy.");
    }
  }

  /**
   * Main method to launch the JavaFX application.
   * @param args command-line arguments.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
