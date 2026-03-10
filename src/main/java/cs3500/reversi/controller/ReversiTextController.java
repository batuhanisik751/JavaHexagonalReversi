package cs3500.reversi.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import cs3500.reversi.model.IReversiModel;
import cs3500.reversi.view.ITextView;

/**
 * Textual controller for a Reversi game. Allows the game to be played with String input.
 * "P" to pass, "M" followed by coordinates (x,y) to move, "Q" to quit.
 */
public class ReversiTextController {

  private final Scanner in;
  private final Appendable out;
  private final ITextView view;
  private final IReversiModel model;

  /**
   * Builds a Reversi Textual Controller.
   * @param model the model that the controller is going to use to the play the game.
   * @param view the text view that is shown during gameplay.
   * @param in the input from the player.
   */
  public ReversiTextController(IReversiModel model, ITextView view, InputStream in) {
    this.model = model;
    this.view = view;
    this.in = new Scanner(in);
    this.out = System.out;
  }

  /**
   * Starts the game.
   */
  public void startGame() {
    boolean gameover = false;
    try {
      while (!gameover) {
        out.append("\n").append(view.toString()).append("\n");
        out.append("Player ").append(model.getCurrentTurn().toString()).append("'s turn\n");
        view.showOptions();
        String move = in.next();
        switch (move) {
          case "P":
            model.passTurn();
            break;
          case "M":
            view.moveEntry();
            try {
              int row = Integer.parseInt(in.next());
              int col = Integer.parseInt(in.next());
              try {
                model.move(row - 1, col - 1, model.getCurrentTurn());
              } catch (IllegalStateException ise) {
                view.invalidMove();
              }
            } catch (NumberFormatException exception) {
              out.append("Please use valid (int int) input for move.\n");
            }
            gameover = model.gameOver();
            break;
          case "Q":
            gameover = true;
            out.append("Game quit\n");
            break;
          default:
            out.append("Invalid entry, try again.\n");
        }
      }
      out.append("\n").append(view.toString()).append("\n");
      view.winner();
    }
    catch (IOException ioe) {
      throw new IllegalStateException("Failed to append", ioe);
    }
  }
}
