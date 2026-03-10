package cs3500.reversi.controller;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

/**
 * Represents the type of player playing the game (human or ai).
 */
public interface PlayerType {

  /**
   * Plays the appropriate move for the type of player.
   * @param model the Reversi game for the move.
   */
  void play(IReversiModel model);

  /**
   * Gets the player this player type is represented by (Player.BLACK or Player.WHITE)
   * @return
   */
  Player getPlayer();
}
