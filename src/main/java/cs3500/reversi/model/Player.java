package cs3500.reversi.model;

import java.util.Objects;

/**
 * Represents a model.Player of the Reversi game.
 */
public enum Player {
  BLACK("X"), WHITE("O");

  private final String type;

  /**
   * The constructor for model.Player.
   */
  Player(String type) {
    this.type = Objects.requireNonNull(type);
  }

  /**
   * The player that has the turn as a string.
   * @return the current player.
   */
  @Override
  public String toString() {
    return this.type;
  }
}
