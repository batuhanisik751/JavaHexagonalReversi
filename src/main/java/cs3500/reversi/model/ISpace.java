package cs3500.reversi.model;

/**
 * Represents the model.Space model.
 */
public interface ISpace {

  /**
   * Turns this space into a String.
   * @return this space as a String
   */
  String toString();

  /**
   * Sets which player has filled this space, and sets empty to false.
   * @param player the model.Player that the space is filled by.
   */
  void setFilled(Player player);

  /**
   * Finds the current player.
   * @return the current player.
   */
  Player getPlayer();

  /**
   * Checks if the space is empty.
   * @return true if it is empty.
   */
  boolean isEmpty();
}
