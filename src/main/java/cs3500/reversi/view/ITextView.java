package cs3500.reversi.view;

/**
 * Represents the View model.
 */
public interface ITextView {

  /**
   * The board as a string.
   * @return the representation of the board as a string.
   */
  @Override
  String toString();

  /**
   * Shows the options that a Player has. Move, pass, or quit.
   */
  void showOptions();

  /**
   * Shows the text to prompt for a move entry.
   */
  void moveEntry();

  /**
   * Shows the text that indicated an invalid move.
   */
  void invalidMove();

  /**
   * Show the text that displays the winner of the game with their score.
   */
  void winner();
}
