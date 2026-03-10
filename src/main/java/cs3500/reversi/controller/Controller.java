package cs3500.reversi.controller;

import java.util.ArrayList;
import java.util.List;

import cs3500.reversi.history.GameHistory;
import cs3500.reversi.model.Coordinate;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.view.IGraphicsView;

/**
 * Represents the controller for the Reversi game. Receives user actions from the view
 * via the {@link ViewListener} interface and updates the model accordingly.
 */
public class Controller implements ViewListener {
  private final IReversiModel model;
  private final IGraphicsView view;
  private final Player player;
  private final GameHistory history;
  private IReversiModel lastSnapshot;

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
    this.view = view;
    this.history = history;
    view.setViewListener(this);
    view.makeVisible();
  }

  /**
   * Starts the Controller, showing the initial turn notification.
   */
  public void start() {
    view.playerTurn();
  }

  @Override
  public void onMove(int row, int col) {
    IReversiModel undoSnapshot = model.copyModel();
    Player[][] before = snapshotBoard();
    try {
      model.move(row, col, model.getCurrentTurn());
    } catch (IllegalStateException ise) {
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
    checkGameOver();
  }

  @Override
  public void onPass() {
    model.passTurn();
    this.lastSnapshot = null;
    history.recordPass(player);
    view.updateHistory(history.getRecords());
    view.highlightLastMove(-1, -1, new ArrayList<>());
    view.refresh();
    checkGameOver();
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

  private void checkGameOver() {
    if (model.gameOver()) {
      view.gameOver(model.getOpponentScore(Player.WHITE),
              model.getOpponentScore(Player.BLACK));
    }
  }
}
