package cs3500.reversi.strategy;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

/**
 * Represents different strategies for moves in Reversi.
 */
public interface IReversiStrategies {

  /**
   * Chooses and plays the next move for the given Player.
   * @param model the reversi game being played.
   * @param player the player the move is for.
   */
  void chooseNextMove(IReversiModel model, Player player);
}
