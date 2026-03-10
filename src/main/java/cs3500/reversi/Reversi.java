package cs3500.reversi;

import cs3500.reversi.controller.AIPlayer;
import cs3500.reversi.controller.Controller;
import cs3500.reversi.controller.HumanPlayer;
import cs3500.reversi.controller.PlayerType;
import cs3500.reversi.history.GameHistory;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.model.ReversiModel;
import cs3500.reversi.view.ReversiGraphicsView;
import cs3500.reversi.strategy.IReversiStrategies;
import cs3500.reversi.strategy.MiniMax;
import cs3500.reversi.strategy.CornersFirst;
import cs3500.reversi.strategy.AvoidNextToCorners;
import cs3500.reversi.strategy.AsManyPiecesAsPossible;
import cs3500.reversi.strategy.AlphaBetaMiniMax;

import cs3500.reversi.view.ClassicTheme;
import cs3500.reversi.view.DarkTheme;
import cs3500.reversi.view.HighContrastTheme;
import cs3500.reversi.view.SetupDialog;
import cs3500.reversi.view.Theme;

/**
 * The main class for running the Reversi game.
 */
public final class Reversi {

  /**
   * The main method to initiate and run the Reversi game with a graphical view.
   * If no arguments are provided, a setup dialog is shown.
   * @param args Optional command line arguments in the format (boardsize player player) where
   *             player is either "human" or "ai". If "ai" is picked, then it must be followed by a
   *             difficulty ("easy", "medium", "hard") or legacy strategy name
   *             ("strategy1", "strategy2", "strategy3", "strategy4").
   *             Ex: (4 human ai easy), (4 human human), (3 ai medium ai hard).
   */
  public static void main(String[] args) {
    if (args.length > 0) {
      startGame(args);
    } else {
      showSetupAndStart();
    }
  }

  private static void showSetupAndStart() {
    SetupDialog dialog = new SetupDialog();
    dialog.setVisible(true);
    if (!dialog.isConfirmed()) {
      System.exit(0);
    }
    startGameFromConfig(dialog.getBoardSize(), dialog.getPlayer1Type(),
            dialog.getPlayer2Type(), resolveTheme(dialog.getThemeName()));
  }

  private static void startGameFromConfig(int boardSize, String p1Type, String p2Type,
                                           Theme theme) {
    IReversiModel model = new ReversiModel(boardSize);
    PlayerType player1 = createPlayer(model, Player.BLACK, p1Type);
    PlayerType player2 = createPlayer(model, Player.WHITE, p2Type);

    ReversiGraphicsView viewPlayer1 = new ReversiGraphicsView(model, Player.BLACK, theme);
    ReversiGraphicsView viewPlayer2 = new ReversiGraphicsView(model, Player.WHITE, theme);

    Runnable restart = () -> {
      viewPlayer1.dispose();
      viewPlayer2.dispose();
      showSetupAndStart();
    };
    viewPlayer1.setRestartAction(restart);
    viewPlayer2.setRestartAction(restart);

    GameHistory history = new GameHistory();
    Controller controller1 = new Controller(model, player1, viewPlayer1, history);
    Controller controller2 = new Controller(model, player2, viewPlayer2, history);
    controller1.start();
    controller2.start();
  }

  private static PlayerType createPlayer(IReversiModel model, Player color, String type) {
    if (type.equals("human")) {
      return new HumanPlayer(model, color);
    } else {
      return new AIPlayer(color, findStrategy(type));
    }
  }

  private static void startGame(String[] args) {
    IReversiModel model = new ReversiModel(Integer.parseInt(args[0]));
    PlayerType player1 = new HumanPlayer(model, Player.BLACK);
    PlayerType player2 = new HumanPlayer(model, Player.WHITE);
    if (args.length < 3) {
      throw new IllegalArgumentException(
              "Please input board size and player types. (size player player)");
    }
    if (args.length == 3) {
      player1 = new HumanPlayer(model, Player.BLACK);
      player2 = new HumanPlayer(model, Player.WHITE);
    } else if (args.length == 4) {
      if (args[1].equals("human")) {
        player1 = new HumanPlayer(model, Player.BLACK);
        player2 = new AIPlayer(Player.WHITE, findStrategy(args[3]));
      } else {
        player1 = new AIPlayer(Player.BLACK, findStrategy(args[2]));
        player2 = new HumanPlayer(model, Player.WHITE);
      }
    } else if (args.length == 5) {
      player1 = new AIPlayer(Player.BLACK, findStrategy(args[2]));
      player2 = new AIPlayer(Player.WHITE, findStrategy(args[4]));
    }

    Theme defaultTheme = new DarkTheme();
    ReversiGraphicsView viewPlayer1 = new ReversiGraphicsView(model, Player.BLACK, defaultTheme);
    ReversiGraphicsView viewPlayer2 = new ReversiGraphicsView(model, Player.WHITE, defaultTheme);

    Runnable restart = () -> {
      viewPlayer1.dispose();
      viewPlayer2.dispose();
      startGame(args);
    };
    viewPlayer1.setRestartAction(restart);
    viewPlayer2.setRestartAction(restart);

    GameHistory history = new GameHistory();
    Controller controller1 = new Controller(model, player1, viewPlayer1, history);
    Controller controller2 = new Controller(model, player2, viewPlayer2, history);
    controller1.start();
    controller2.start();
  }


  private static Theme resolveTheme(String name) {
    switch (name) {
      case "classic":
        return new ClassicTheme();
      case "highcontrast":
        return new HighContrastTheme();
      default:
        return new DarkTheme();
    }
  }

  // gets the corresponding strategy to what is being inputted
  private static IReversiStrategies findStrategy(String strategy) {
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

}