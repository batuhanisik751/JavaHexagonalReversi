package cs3500.reversi.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;

import cs3500.reversi.history.GameHistory;
import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.network.ClientListener;
import cs3500.reversi.network.ReversiClient;
import cs3500.reversi.persistence.GameSaver;
import cs3500.reversi.view.IGraphicsView;

/**
 * Controller for networked games on the client side. Implements both
 * {@link ViewListener} (to receive local UI events) and {@link ClientListener}
 * (to receive server events). Forwards user actions to the server and applies
 * state updates received back to the local model copy used for rendering.
 */
public class NetworkController implements ViewListener, ClientListener {
  private final IReversiModel localModel;
  private final IGraphicsView view;
  private final ReversiClient client;
  private final GameHistory history;
  private boolean myTurn;

  /**
   * Creates a network controller.
   * @param localModel a local model instance used for rendering (updated via loadState).
   * @param view the local graphics view.
   * @param client the connected network client.
   * @param history the game history log.
   */
  public NetworkController(IReversiModel localModel, IGraphicsView view,
                           ReversiClient client, GameHistory history) {
    this.localModel = localModel;
    this.view = view;
    this.client = client;
    this.history = history;
    this.myTurn = false;
    view.setViewListener(this);
    client.setListener(this);
  }

  /** Starts the controller: makes the view visible and begins listening for server messages. */
  public void start() {
    view.makeVisible();
    client.startListening();
  }

  // ---------------------------------------------------------------------------
  // ViewListener — forward user actions to server
  // ---------------------------------------------------------------------------

  @Override
  public void onMove(int row, int col) {
    if (myTurn) {
      client.sendMove(row, col);
    }
  }

  @Override
  public void onPass() {
    if (myTurn) {
      client.sendPass();
    }
  }

  @Override
  public void onUndo() {
    client.sendUndo();
  }

  @Override
  public void onSave() {
    File file = view.showSaveFileChooser();
    if (file != null) {
      try {
        GameSaver.save(localModel, history, file);
        view.showSaveSuccess();
      } catch (IOException e) {
        view.showFileError("Failed to save: " + e.getMessage());
      }
    }
  }

  @Override
  public void onLoad() {
    view.showFileError("Cannot load a saved game during network play.");
  }

  // ---------------------------------------------------------------------------
  // ClientListener — receive server events (called on reader thread)
  // ---------------------------------------------------------------------------

  @Override
  public void onStateUpdate(int boardSize, Player currentTurn, Player[][] boardState) {
    Platform.runLater(() -> {
      localModel.loadState(currentTurn, boardState);
      view.refresh();
    });
  }

  @Override
  public void onYourTurn() {
    Platform.runLater(() -> {
      myTurn = true;
      view.playerTurn();
    });
  }

  @Override
  public void onWait() {
    Platform.runLater(() -> {
      myTurn = false;
      view.refresh();
    });
  }

  @Override
  public void onMoveMade(Player player, int row, int col, int flippedCount) {
    Platform.runLater(() -> {
      history.recordMove(player, row, col, new ArrayList<>());
      view.updateHistory(history.getRecords());
    });
  }

  @Override
  public void onPassMade(Player player) {
    Platform.runLater(() -> {
      history.recordPass(player);
      view.updateHistory(history.getRecords());
    });
  }

  @Override
  public void onUndoOk() {
    Platform.runLater(() -> {
      history.undoLast();
      view.updateHistory(history.getRecords());
      view.highlightLastMove(-1, -1, new ArrayList<>());
    });
  }

  @Override
  public void onUndoDenied(String reason) {
    Platform.runLater(() -> view.undoNotAvailableMessage());
  }

  @Override
  public void onInvalidMove(String reason) {
    Platform.runLater(() -> view.invalidMoveMessage());
  }

  @Override
  public void onGameOver(int blackScore, int whiteScore) {
    Platform.runLater(() -> view.gameOver(blackScore, whiteScore));
  }

  @Override
  public void onOpponentDisconnected() {
    Platform.runLater(() -> view.showFileError("Opponent disconnected."));
  }

  @Override
  public void onConnectionError(String message) {
    Platform.runLater(() -> view.showFileError("Connection lost: " + message));
  }
}
