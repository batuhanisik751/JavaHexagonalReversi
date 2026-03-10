package cs3500.reversi.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import cs3500.reversi.history.GameHistory;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.persistence.GameLoader;
import cs3500.reversi.persistence.GameSaver;
import cs3500.reversi.persistence.LoadResult;
import cs3500.reversi.audio.SoundManager;
import cs3500.reversi.view.IGraphicsView;

/**
 * Represents the controller for the Reversi game. Receives user actions from the view
 * via the {@link ViewListener} interface and updates the model accordingly.
 */
public class Controller implements ViewListener {
  private final IReversiModel model;
  private final IGraphicsView view;
  private final Player player;
  private final PlayerType playerType;
  private final GameHistory history;
  private IReversiModel lastSnapshot;
  private Controller opponent;

  /**
   * Makes a Controller for a reversi model and player.
   * @param model the reversi board/game to be played.
   * @param player the human/ai player that is using the controller.
   * @param view the view of the reversi model.
   * @param history the shared game history log.
   */
  public Controller(IReversiModel model, PlayerType player, IGraphicsView view,
                    GameHistory history) {
    this.model = model;
    this.player = player.getPlayer();
    this.playerType = player;
    this.view = view;
    this.history = history;
    view.setViewListener(this);
    view.makeVisible();
  }

  /**
   * Sets the opponent controller so that AI moves can chain between players.
   * @param opponent the other player's controller.
   */
  public void setOpponent(Controller opponent) {
    this.opponent = opponent;
  }

  /**
   * Starts the Controller, showing the initial turn notification.
   * If this controller's player is AI and it's their turn, triggers AI play.
   */
  public void start() {
    view.playerTurn();
    tryAIMove();
  }

  private void tryAIMove() {
    if (model.gameOver()) {
      return;
    }
    if (playerType instanceof AIPlayer && model.getCurrentTurn() == player) {
      Timer timer = new Timer(300, e -> {
        if (!model.gameOver() && model.getCurrentTurn() == player) {
          try {
            playerType.play(model);
          } catch (Exception ex) {
            // Strategy failed — fall back to passing so the game continues.
            model.passTurn();
          }
          view.refresh();
          SoundManager.play("move");
          checkGameOver();
          notifyOpponent();
        }
      });
      timer.setRepeats(false);
      timer.start();
    }
  }

  /**
   * Notifies the opponent controller to refresh its view and attempt its AI move.
   */
  private void notifyOpponent() {
    if (opponent != null) {
      opponent.view.refresh();
      opponent.tryAIMove();
    }
  }

  @Override
  public void onMove(int row, int col) {
    IReversiModel undoSnapshot = model.copyModel();
    Player[][] before = snapshotBoard();
    try {
      model.move(row, col, model.getCurrentTurn());
    } catch (IllegalStateException ise) {
      SoundManager.play("invalid");
      view.invalidMoveMessage();
      view.refresh();
      return;
    }
    this.lastSnapshot = undoSnapshot;
    List<Coordinate> flipped = new ArrayList<>();
    for (int r = 0; r < model.getBoard().size(); r++) {
      for (int c = 0; c < model.getRow(r).size(); c++) {
        if (r == row && c == col) {
          continue;
        }
        Player afterPlayer = model.getSpaceContent(r, c);
        if (afterPlayer != null && afterPlayer != before[r][c]) {
          flipped.add(new Coordinate(r, c));
        }
      }
    }
    history.recordMove(player, row, col, flipped);
    view.updateHistory(history.getRecords());
    view.highlightLastMove(row, col, flipped);
    view.refresh();
    SoundManager.play("move");
    checkGameOver();
    notifyOpponent();
    tryAIMove();
  }

  @Override
  public void onPass() {
    model.passTurn();
    this.lastSnapshot = null;
    history.recordPass(player);
    view.updateHistory(history.getRecords());
    view.highlightLastMove(-1, -1, new ArrayList<>());
    view.refresh();
    SoundManager.play("pass");
    checkGameOver();
    notifyOpponent();
    tryAIMove();
  }

  @Override
  public void onUndo() {
    if (lastSnapshot == null) {
      view.undoNotAvailableMessage();
      return;
    }
    model.restoreFrom(lastSnapshot);
    lastSnapshot = null;
    history.undoLast();
    view.updateHistory(history.getRecords());
    view.highlightLastMove(-1, -1, new ArrayList<>());
    view.refresh();
    SoundManager.play("undo");
  }

  /**
   * Checks if the game is over and shows the game over message if so.
   */
  private Player[][] snapshotBoard() {
    int rows = model.getBoard().size();
    Player[][] snapshot = new Player[rows][];
    for (int r = 0; r < rows; r++) {
      int cols = model.getRow(r).size();
      snapshot[r] = new Player[cols];
      for (int c = 0; c < cols; c++) {
        snapshot[r][c] = model.getSpaceContent(r, c);
      }
    }
    return snapshot;
  }

  @Override
  public void onSave() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new FileNameExtensionFilter("Reversi Save (.reversi)", "reversi"));
    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
      try {
        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".reversi")) {
          file = new File(file.getAbsolutePath() + ".reversi");
        }
        GameSaver.save(model, history, file);
        view.showSaveSuccess();
      } catch (IOException e) {
        view.showFileError("Failed to save: " + e.getMessage());
      }
    }
  }

  @Override
  public void onLoad() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new FileNameExtensionFilter("Reversi Save (.reversi)", "reversi"));
    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      try {
        LoadResult result = GameLoader.load(chooser.getSelectedFile());
        if (result.getBoardSize() != model.getBoardSize()) {
          view.showFileError("Save file board size (" + result.getBoardSize()
                  + ") doesn't match current game (" + model.getBoardSize() + ").");
          return;
        }
        model.loadState(result.getCurrentTurn(), result.getBoardState());
        this.lastSnapshot = null;
        history.clear();
        history.loadRecords(result.getHistory());
        view.updateHistory(history.getRecords());
        view.highlightLastMove(-1, -1, new ArrayList<>());
        view.refresh();
        view.showLoadSuccess();
      } catch (IOException e) {
        view.showFileError("Failed to load: " + e.getMessage());
      }
    }
  }

  private void checkGameOver() {
    if (model.gameOver()) {
      SoundManager.play("gameOver");
      view.gameOver(model.getOpponentScore(Player.WHITE),
              model.getOpponentScore(Player.BLACK));
    }
  }
}
