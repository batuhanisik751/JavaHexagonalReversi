package cs3500.reversi.model;

/**
 * Represents a playable space on the board.
 */
public class Space implements ISpace {
  private Player filled;

  /**
   * Builds a Space, default Space is set to empty (no player).
   */
  public Space() {
    this.filled = null;
  }

  /**
   * Turns this space into a String.
   * @return this space as a String
   */
  @Override
  public String toString() {
    if (filled == null) {
      return "_ ";
    } else {
      return filled.toString() + " ";
    }
  }

  /**
   * Sets which player has filled this space.
   * @param player the Player that the space is filled by.
   */
  public void setFilled(Player player) {
    this.filled = player;
  }

  /**
   * Checks if this Space is empty.
   * @return true if this Space is empty, false if filled.
   */
  @Override
  public boolean isEmpty() {
    return this.filled == null;
  }

  /**
   * Gets the Player that has filled this Space.
   * @return Player that filled the Space, or null if empty.
   */
  @Override
  public Player getPlayer() {
    return this.filled;
  }
}
