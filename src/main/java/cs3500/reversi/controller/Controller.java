package cs3500.reversi.controller;

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

  /**
   * Makes a Controller for a reversi model and player.
   * @param model the reversi board/game to be played.
   * @param player the human/ai player that is using the controller.
   * @param view the view of the reversi model.
   */
  public Controller(IReversiModel model, PlayerType player, IGraphicsView view) {
    this.model = model;
    this.view = view;
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
    try {
      model.move(row, col, model.getCurrentTurn());
    } catch (IllegalStateException ise) {
      view.invalidMoveMessage();
    }
    view.refresh();
    checkGameOver();
  }

  @Override
  public void onPass() {
    model.passTurn();
    view.refresh();
    checkGameOver();
  }

  /**
   * Checks if the game is over and shows the game over message if so.
   */
  private void checkGameOver() {
    if (model.gameOver()) {
      view.gameOver(model.getOpponentScore(Player.WHITE),
              model.getOpponentScore(Player.BLACK));
    }
  }
}
