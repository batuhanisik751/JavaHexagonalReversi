package cs3500.reversi.controller;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;
import cs3500.reversi.strategy.IReversiStrategies;

/**
 * Represents an ai player of the Reversi game.
 */
public class AIPlayer implements PlayerType {
  private final Player player;
  private IReversiStrategies strategy;

  /**
   * Constructs an ai player.
   * @param player the Player.BLACK or Player.WHITE that is controlled by the ai.
   * @param strategy the strategy that ai will be using for their turn.
   */
  public AIPlayer(Player player, IReversiStrategies strategy) {
    this.player = player;
    this.strategy = strategy;
  }

  /**
   * Plays the move with the strategy given to the ai.
   * @param model the Reversi game for the move.
   */
  @Override
  public void play(IReversiModel model) {
    strategy.chooseNextMove(model, player);
  }

  /**
   * Gets the player that is controlled by the ai.
   * @return player that is controlled by the ai (Player.BLACK or Player.WHITE)
   */
  @Override
  public Player getPlayer() {
    return this.player;
  }
}
