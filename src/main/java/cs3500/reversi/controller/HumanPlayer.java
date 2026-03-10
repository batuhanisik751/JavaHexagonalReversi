package cs3500.reversi.controller;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

/**
 * Represents a human player for the game.
 */
public class HumanPlayer implements PlayerType {
  private final Player player;

  /**
   * Constructs a human player for the game.
   * @param model the Reversi game to be played on.
   * @param player the player that this human player is controlling (Player.WHITE or Player.BLACK)
   */
  public HumanPlayer(IReversiModel model, Player player) {
    this.player = player;
  }

  /**
   * Plays the move for this human player.
   * @param model the Reversi game for the move.
   */
  @Override
  public void play(IReversiModel model) {
    return;
  }

  /**
   * Gets the player that this human player is controlling.
   * @return the player that is being controlled (Player.BLACK or player.WHITE)
   */
  @Override
  public Player getPlayer() {
    return player;
  }

}
