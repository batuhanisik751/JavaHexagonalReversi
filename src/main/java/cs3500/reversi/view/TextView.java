package cs3500.reversi.view;

import java.io.PrintStream;

import cs3500.reversi.model.IReadOnlyReversiModel;
import cs3500.reversi.model.Player;

/**
 * Represents a view of a model.IReversiModel.
 */
public class TextView implements ITextView {
  private final IReadOnlyReversiModel model;
  private final PrintStream out;

  /**
   * Creates a modelView.IModelView with the given model.
   * @param model model.IReversiModel to be seen in the view.
   */
  public TextView(IReadOnlyReversiModel model) {
    this.model = model;
    this.out = System.out;
  }

  /**
   * The board as a string.
   * @return the representation of the board as a string.
   */
  @Override
  public String toString() {
    String shapeName = model.getBoardShape().getShapeName();
    switch (shapeName) {
      case "square":
        return toStringSquare();
      case "triangular":
        return toStringTriangular();
      default:
        return toStringHex();
    }
  }

  private String toStringHex() {
    int boardSize = model.getBoardSize();
    StringBuilder stringBoard = new StringBuilder();

    for (int row = 0; row < (boardSize * 2) - 1; row++) {
      int numRep = Math.abs(row - boardSize);
      if (row > boardSize - 1) {
        numRep = numRep + 2;
      }
      stringBoard.append(" ".repeat(numRep));
      for (int space = 0; space < model.getRow(row).size(); space++) {
        stringBoard.append(model.getRow(row).get(space).toString());
      }
      stringBoard.append(" ".repeat(numRep)).append("\n");
    }
    return stringBoard.toString();
  }

  private String toStringSquare() {
    StringBuilder sb = new StringBuilder();
    for (int row = 0; row < model.getBoard().size(); row++) {
      for (int col = 0; col < model.getRow(row).size(); col++) {
        sb.append(model.getRow(row).get(col).toString());
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  private String toStringTriangular() {
    StringBuilder sb = new StringBuilder();
    int totalRows = model.getBoard().size();
    int maxWidth = 2 * totalRows - 1;
    for (int row = 0; row < totalRows; row++) {
      int cellsInRow = model.getRow(row).size();
      int indent = maxWidth - cellsInRow;
      sb.append(" ".repeat(indent));
      for (int col = 0; col < cellsInRow; col++) {
        sb.append(model.getRow(row).get(col).toString());
      }
      sb.append("\n");
    }
    return sb.toString();
  }


  /**
   * Shows the options that a Player has. Move, pass, or quit.
   */
  @Override
  public void showOptions() {
    out.println("M: Make a move");
    out.println("P: Pass turn");
    out.println("Q: Quit the program\n");
    out.print("Enter your choice: ");
  }


  /**
   * Shows the text to prompt for a move entry.
   */
  @Override
  public void moveEntry() {
    out.print("Enter move (row column) : ");
  }


  /**
   * Shows the text that indicated an invalid move.
   */
  @Override
  public void invalidMove() {
    out.println("Invalid move : Play again.");
  }


  /**
   * Show the text that displays the winner of the game with their score.
   */
  @Override
  public void winner() {
    if (model.getScore(Player.WHITE) > model.getScore(Player.BLACK)) {
      out.println("Player O wins! Score : " + model.getScore(Player.WHITE));
    } else if (model.getScore(Player.BLACK) == model.getScore(Player.WHITE)) {
      out.println("Score is tied! Score : " + model.getScore(Player.WHITE));
    } else {
      out.println("Player X wins! Score : " + model.getScore(Player.BLACK));
    }
  }
}