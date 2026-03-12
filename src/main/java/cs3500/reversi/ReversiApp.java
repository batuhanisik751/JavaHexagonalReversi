package cs3500.reversi;

import java.io.IOException;
import java.net.InetAddress;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import cs3500.reversi.controller.AIPlayer;
import cs3500.reversi.controller.Controller;
import cs3500.reversi.controller.HumanPlayer;
import cs3500.reversi.controller.NetworkController;
import cs3500.reversi.controller.PlayerType;
import cs3500.reversi.history.GameHistory;
import cs3500.reversi.model.HexBoardShape;
import cs3500.reversi.model.IBoardShape;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.model.SquareBoardShape;
import cs3500.reversi.model.TriangularBoardShape;
import cs3500.reversi.network.ReversiClient;
import cs3500.reversi.network.ReversiServer;
import cs3500.reversi.strategy.AlphaBetaMiniMax;
import cs3500.reversi.strategy.AsManyPiecesAsPossible;
import cs3500.reversi.strategy.AvoidNextToCorners;
import cs3500.reversi.strategy.CornersFirst;
import cs3500.reversi.strategy.IReversiStrategies;
import cs3500.reversi.strategy.MiniMax;
import cs3500.reversi.strategy.ShapeAwareAvoidNextToCorners;
import cs3500.reversi.strategy.ShapeAwareCornersFirst;
import cs3500.reversi.view.FxClassicTheme;
import cs3500.reversi.view.FxDarkTheme;
import cs3500.reversi.view.FxHighContrastTheme;
import cs3500.reversi.view.FxReversiView;
import cs3500.reversi.view.FxSetupDialog;
import cs3500.reversi.stats.GameMetadata;
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

    String mode = dialog.getGameMode();
    FxTheme theme = resolveTheme(dialog.getThemeName());
    String shapeName = dialog.getShapeName();
    int timerSeconds = dialog.getTimerSeconds();

    switch (mode) {
      case "host":
        startHostGame(primaryStage, dialog.getBoardSize(), dialog.getPlayer1Type(),
                dialog.getPort(), theme, timerSeconds, shapeName);
        break;
      case "join":
        startJoinGame(primaryStage, dialog.getHostAddress(), dialog.getPort(), theme,
                timerSeconds);
        break;
      default:
        startLocalGame(primaryStage, dialog.getBoardSize(), dialog.getPlayer1Type(),
                dialog.getPlayer2Type(), theme, timerSeconds, shapeName);
        break;
    }
  }

  private void startLocalGame(Stage primaryStage, int boardSize,
                               String p1Type, String p2Type, FxTheme theme,
                               int timerSeconds, String shapeName) {
    IBoardShape shape = resolveShape(shapeName);
    IReversiModel model = new ReversiModel(boardSize, shape);
    PlayerType player1 = createPlayer(model, Player.BLACK, p1Type, shapeName);
    PlayerType player2 = createPlayer(model, Player.WHITE, p2Type, shapeName);

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
    GameMetadata metadata = new GameMetadata(boardSize, p1Type, p2Type);
    Controller controller1 = new Controller(model, player1, viewPlayer1, history);
    Controller controller2 = new Controller(model, player2, viewPlayer2, history);
    controller1.setGameMetadata(metadata);
    controller2.setGameMetadata(metadata);
    controller1.setTimerSeconds(timerSeconds);
    controller2.setTimerSeconds(timerSeconds);
    controller1.setOpponent(controller2);
    controller2.setOpponent(controller1);
    controller1.start();
    controller2.start();
  }

  private void startHostGame(Stage primaryStage, int boardSize, String p1Type,
                              int port, FxTheme theme, int timerSeconds, String shapeName) {
    try {
      IBoardShape shape = resolveShape(shapeName);
      // Start the server
      ReversiServer server = new ReversiServer(port, boardSize, 60000);
      server.start();
      int actualPort = server.getLocalPort();

      // Show waiting message with connection info
      String localAddress;
      try {
        localAddress = InetAddress.getLocalHost().getHostAddress();
      } catch (Exception e) {
        localAddress = "localhost";
      }

      // Connect the host as a client via loopback
      ReversiClient hostClient = new ReversiClient("localhost", actualPort);
      Player hostColor = hostClient.connect();

      // Create local model for rendering
      IReversiModel localModel = new ReversiModel(boardSize, shape);
      FxReversiView hostView = new FxReversiView(localModel, hostColor, theme, primaryStage);
      hostView.setStatusMessage("Hosting on " + localAddress + ":" + actualPort
              + " — waiting for opponent...");
      hostView.makeVisible();

      // Set up restart to also stop server
      Runnable cleanup = () -> {
        server.stop();
        hostClient.disconnect();
        primaryStage.close();
        showSetupAndStart(new Stage());
      };
      hostView.setRestartAction(cleanup);

      server.setListener(new ReversiServer.ServerListener() {
        @Override
        public void onServerLog(String message) {
          // no-op for now
        }

        @Override
        public void onClientDisconnect(Player disconnectedPlayer) {
          // Server-side logging; client-side dialog handled by NetworkController
        }
      });

      GameHistory history = new GameHistory();
      NetworkController netController = new NetworkController(localModel, hostView,
              hostClient, history);
      netController.setDisconnectAction(cleanup);
      netController.setTimerSeconds(timerSeconds);
      hostClient.startListening();

    } catch (IOException e) {
      showError("Failed to start server: " + e.getMessage());
    }
  }

  private void startJoinGame(Stage primaryStage, String host, int port, FxTheme theme,
                              int timerSeconds) {
    try {
      ReversiClient client = new ReversiClient(host, port);
      Player myColor = client.connect();

      // Create local model with the server's actual board size
      int serverBoardSize = client.getServerBoardSize();
      IReversiModel localModel = new ReversiModel(serverBoardSize);
      FxReversiView view = new FxReversiView(localModel, myColor, theme, primaryStage);
      view.setStatusMessage("Connected as " + myColor.name() + " — waiting for game to start...");

      Runnable cleanup = () -> {
        client.disconnect();
        primaryStage.close();
        showSetupAndStart(new Stage());
      };
      view.setRestartAction(cleanup);

      GameHistory history = new GameHistory();
      NetworkController netController = new NetworkController(localModel, view, client, history);
      netController.setDisconnectAction(cleanup);
      netController.setTimerSeconds(timerSeconds);
      netController.start();

    } catch (IOException e) {
      showError("Failed to connect: " + e.getMessage());
    }
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Network Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private PlayerType createPlayer(IReversiModel model, Player color, String type,
                                  String shapeName) {
    if (type.equals("human")) {
      return new HumanPlayer(model, color);
    } else {
      return new AIPlayer(color, findStrategy(type, shapeName));
    }
  }

  private IBoardShape resolveShape(String name) {
    switch (name) {
      case "square":
        return new SquareBoardShape();
      case "triangular":
        return new TriangularBoardShape();
      default:
        return new HexBoardShape();
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

  private IReversiStrategies findStrategy(String strategy, String shapeName) {
    boolean isHex = "hexagonal".equals(shapeName);
    switch (strategy) {
      case "easy":
      case "strategy1":
        return new AsManyPiecesAsPossible();
      case "medium":
      case "strategy3":
        return isHex ? new CornersFirst() : new ShapeAwareCornersFirst();
      case "hard":
        return new AlphaBetaMiniMax(3);
      case "strategy2":
        return isHex ? new AvoidNextToCorners() : new ShapeAwareAvoidNextToCorners();
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
