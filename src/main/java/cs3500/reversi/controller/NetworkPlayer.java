package cs3500.reversi.controller;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.model.Player;

/**
 * A player type representing a remote human player connected over the network.
 * Like {@link HumanPlayer}, the play() method is a no-op because moves arrive
 * from the server via network messages rather than local strategy execution.
 */
public class NetworkPlayer implements PlayerType {
  private final Player player;

  public NetworkPlayer(Player player) {
    this.player = player;
  }

  @Override
  public void play(IReversiModel model) {
    // No-op. Moves come from the remote client through the server.
  }

  @Override
  public Player getPlayer() {
    return player;
  }
}
